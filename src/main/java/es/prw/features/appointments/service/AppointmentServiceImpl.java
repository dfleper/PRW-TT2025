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

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud inválida");
        }

        // Seguridad extra: aunque el DTO tenga @NotNull, aquí aseguramos 400 si llega mal
        if (dto.getVehicleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar un vehículo");
        }
        if (dto.getServiceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar un servicio");
        }
        if (dto.getStartDateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar fecha y hora");
        }

        // ===== CHECK: vehículo pertenece al cliente =====
        Long customerId = getCurrentCustomerIdOrThrow();

        VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        Long vehicleCustomerId = (vehicle.getCustomer() != null) ? vehicle.getCustomer().getIdCustomer() : null;

        if (vehicleCustomerId == null || !vehicleCustomerId.equals(customerId)) {
            // 403 válido (tu checklist permite 403 o 404)
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El vehículo no pertenece al cliente");
        }

        // ===== CHECK: calcular fin =====
        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        if (!service.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El servicio no está activo");
        }

        long durationMinutes = 60L; // default seguro
        Short minutosEstimados = service.getMinutosEstimados();
        if (minutosEstimados != null && minutosEstimados > 0) {
            durationMinutes = minutosEstimados.longValue();
        }

        LocalDateTime startDateTime = dto.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        // ===== CHECK: disponibilidad =====
        boolean available = availabilityService.isAvailable(startDateTime, endDateTime);
        if (!available) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay disponibilidad para esa franja horaria");
        }

        // ===== CHECK: guardar cita con estado inicial =====
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setCustomer(vehicle.getCustomer()); // ya validado
        appointment.setVehicle(vehicle);
        appointment.setService(service);
        appointment.setInicio(startDateTime);
        appointment.setFin(endDateTime);
        appointment.setEstado(AppointmentStatus.PENDIENTE);

        // (Opcional) Auditoría: si quieres guardar el usuario creador/modificador.
        // OJO: tu AppointmentEntity tiene createdByUser/updatedByUser, pero no debe ser obligatorio.
        // UserEntity currentUser = getCurrentUserOrThrow();
        // appointment.setCreatedByUser(currentUser);
        // appointment.setUpdatedByUser(currentUser);

        AppointmentEntity saved = appointmentRepository.save(appointment);
        return saved.getId();
    }

    private Long getCurrentCustomerIdOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
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

    // Si luego quieres usar auditoría en AppointmentEntity, activa este helper.
    /*
    private UserEntity getCurrentUserOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no válido"));
    }
    */
}
