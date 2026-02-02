package es.prw.features.appointments.service;

import java.time.LocalDateTime;
import java.util.List;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.dto.AppointmentCreateDto;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
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

    // =========================
    // T11: Crear cita
    // =========================
    @Override
    public Long createAppointment(AppointmentCreateDto dto) {

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud inválida");
        }

        if (dto.getVehicleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar un vehículo");
        }
        if (dto.getServiceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar un servicio");
        }
        if (dto.getStartDateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar fecha y hora");
        }

        Long customerId = getCurrentCustomerIdOrThrow();

        VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        Long vehicleCustomerId = (vehicle.getCustomer() != null) ? vehicle.getCustomer().getIdCustomer() : null;

        if (vehicleCustomerId == null || !vehicleCustomerId.equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El vehículo no pertenece al cliente");
        }

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));

        if (!service.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El servicio no está activo");
        }

        long durationMinutes = 60L;
        Short minutosEstimados = service.getMinutosEstimados();
        if (minutosEstimados != null && minutosEstimados > 0) {
            durationMinutes = minutosEstimados.longValue();
        }

        LocalDateTime startDateTime = dto.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        boolean available = availabilityService.isAvailable(startDateTime, endDateTime);
        if (!available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay disponibilidad para esa franja horaria");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setCustomer(vehicle.getCustomer());
        appointment.setVehicle(vehicle);
        appointment.setService(service);
        appointment.setInicio(startDateTime);
        appointment.setFin(endDateTime);
        appointment.setEstado(AppointmentStatus.PENDIENTE);

        AppointmentEntity saved = appointmentRepository.save(appointment);
        return saved.getId();
    }

    // =========================
    // T12: Mis citas (listado)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEntity> listMyAppointments() {
        Long customerId = getCurrentCustomerIdOrThrow();
        return appointmentRepository.findByCustomer_IdCustomerOrderByInicioDesc(customerId);
    }

    // =========================
    // T12: Mis citas (detalle)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public AppointmentEntity getMyAppointment(Long appointmentId) {
        if (appointmentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id de cita inválido");
        }

        Long customerId = getCurrentCustomerIdOrThrow();

        return appointmentRepository.findDetailByIdAndCustomer(appointmentId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    // =========================
    // T12: Mis citas (cancelar)
    // =========================
    @Override
    public void cancelMyAppointment(Long appointmentId) {
        AppointmentEntity a = getMyAppointment(appointmentId);

        // Regla: solo cancelar si NO está finalizada y NO está en curso (recomendado)
        if (a.getEstado() == AppointmentStatus.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita finalizada");
        }
        if (a.getEstado() == AppointmentStatus.EN_CURSO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita en curso");
        }
        if (a.getEstado() == AppointmentStatus.CANCELADA) {
            // idempotente: si ya está cancelada, no hacemos nada
            return;
        }

        a.setEstado(AppointmentStatus.CANCELADA);
        appointmentRepository.save(a);
    }

    // =========================
    // helper: cliente logueado
    // =========================
    private Long getCurrentCustomerIdOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = auth.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        CustomerEntity customer = customerRepository.findByUser_IdUser(user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es cliente"));

        return customer.getIdCustomer();
    }
}
