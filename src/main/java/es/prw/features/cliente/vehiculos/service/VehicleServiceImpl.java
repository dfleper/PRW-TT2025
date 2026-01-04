package es.prw.features.cliente.vehiculos.service;

import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.cliente.vehiculos.dto.VehicleDto;
import es.prw.features.cliente.vehiculos.exception.DuplicateMatriculaException;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserRepository userRepository,
                              CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> listForClient(String userEmail) {
        CustomerEntity customer = getCustomerByEmail(userEmail);

        return vehicleRepository
                .findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(customer.getIdCustomer())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDto getForEdit(String userEmail, Long idVehicle) {
        CustomerEntity customer = getCustomerByEmail(userEmail);

        VehicleEntity v = vehicleRepository
                .findByIdVehicleAndCustomer_IdCustomer(idVehicle, customer.getIdCustomer())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return toDto(v);
    }

    @Override
    public void create(String userEmail, VehicleDto dto) {
        CustomerEntity customer = getCustomerByEmail(userEmail);

        String matriculaNorm = normalizeMatricula(dto.getMatricula());
        if (vehicleRepository.existsByMatriculaIgnoreCase(matriculaNorm)) {
            throw new DuplicateMatriculaException("Ya existe un vehículo con esa matrícula");
        }

        VehicleEntity v = new VehicleEntity();
        v.setCustomer(customer);
        v.setMatricula(matriculaNorm);
        v.setMarca(dto.getMarca().trim());
        v.setModelo(dto.getModelo().trim());
        v.setAnio(dto.getAnio() == null ? null : dto.getAnio().shortValue()); // Integer -> Short
        v.setCombustible(trimToNull(dto.getCombustible()));
        v.setNotas(trimToNull(dto.getNotas()));
        v.setActivo(true);

        vehicleRepository.save(v);
    }

    @Override
    public void update(String userEmail, Long idVehicle, VehicleDto dto) {
        CustomerEntity customer = getCustomerByEmail(userEmail);

        VehicleEntity v = vehicleRepository
                .findByIdVehicleAndCustomer_IdCustomer(idVehicle, customer.getIdCustomer())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String matriculaNorm = normalizeMatricula(dto.getMatricula());
        if (vehicleRepository.existsByMatriculaIgnoreCaseAndIdVehicleNot(matriculaNorm, idVehicle)) {
            throw new DuplicateMatriculaException("Ya existe un vehículo con esa matrícula");
        }

        v.setMatricula(matriculaNorm);
        v.setMarca(dto.getMarca().trim());
        v.setModelo(dto.getModelo().trim());
        v.setAnio(dto.getAnio() == null ? null : dto.getAnio().shortValue()); // Integer -> Short
        v.setCombustible(trimToNull(dto.getCombustible()));
        v.setNotas(trimToNull(dto.getNotas()));

        vehicleRepository.save(v);
    }

    @Override
    public void delete(String userEmail, Long idVehicle) {
        CustomerEntity customer = getCustomerByEmail(userEmail);

        VehicleEntity v = vehicleRepository
                .findByIdVehicleAndCustomer_IdCustomer(idVehicle, customer.getIdCustomer())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        v.setActivo(false); // soft delete
        vehicleRepository.save(v);
    }

    // ---------- helpers ----------
    private CustomerEntity getCustomerByEmail(String email) {
        UserEntity user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return customerRepository.findByUser_IdUser(user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    private VehicleDto toDto(VehicleEntity v) {
        VehicleDto dto = new VehicleDto();
        dto.setId(v.getIdVehicle());
        dto.setMatricula(v.getMatricula());
        dto.setMarca(v.getMarca());
        dto.setModelo(v.getModelo());
        dto.setAnio(v.getAnio() == null ? null : v.getAnio().intValue()); // Short -> Integer
        dto.setCombustible(v.getCombustible());
        dto.setNotas(v.getNotas());
        return dto;
    }

    private String normalizeMatricula(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase();
        return s.replace(" ", "").replace("-", "");
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
