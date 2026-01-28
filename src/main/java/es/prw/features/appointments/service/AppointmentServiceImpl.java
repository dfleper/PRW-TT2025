package es.prw.features.appointments.service;

import java.time.LocalDateTime;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.dto.AppointmentCreateDto;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final AvailabilityService availabilityService;
    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            ServiceRepository serviceRepository,
            AvailabilityService availabilityService,
            AppointmentRepository appointmentRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.availabilityService = availabilityService;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Long createAppointment(AppointmentCreateDto dto) {

        // ===== CHECK 2: vehículo pertenece al cliente =====
        Long customerId = getCurrentCustomerIdOrThrow();

        VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        if (vehicle.getCustomer() == null
                || vehicle.getCustomer().getIdCustomer() == null
                || !vehicle.getCustomer().getIdCustomer().equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El vehículo no pertenece al cliente");
        }

        // ===== CHECK 3: calcular fin =====
        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        if (!service.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El servicio no está activo");
        }

        Short durationMinutesShort = service.getMinutosEstimados();
        long durationMinutes = 60L; // valor por defecto
        if (durationMinutesShort != null && durationMinutesShort > 0) {
            durationMinutes = durationMinutesShort.longValue();
        }

        LocalDateTime startDateTime = dto.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        // ===== CHECK 4: disponibilidad =====
        boolean available = availabilityService.isAvailable(startDateTime, endDateTime);
        if (!available) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay disponibilidad para esa franja horaria");
        }

        // ===== CHECK 5: guardar cita con estado inicial =====
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setCustomer(vehicle.getCustomer()); // ya validado
        appointment.setVehicle(vehicle);
        appointment.setService(service);
        appointment.setInicio(startDateTime);
        appointment.setFin(endDateTime);
        appointment.setEstado(AppointmentStatus.PENDIENTE);

        AppointmentEntity saved = appointmentRepository.save(appointment);
        return saved.getId();
    }

    private Long getCurrentCustomerIdOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = auth.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        CustomerEntity customer = customerRepository.findByUser_IdUser(user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El usuario no es cliente"));

        return customer.getIdCustomer();
    }
}
