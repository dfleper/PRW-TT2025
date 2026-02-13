package es.prw.features.appointments.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
import es.prw.features.notifications.service.AppointmentMailData;
import es.prw.features.notifications.service.EmailService;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

	private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

	private final VehicleRepository vehicleRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ServiceRepository serviceRepository;
	private final AvailabilityService availabilityService;
	private final AppointmentRepository appointmentRepository;
	private final EmailService emailService;

	public AppointmentServiceImpl(VehicleRepository vehicleRepository, UserRepository userRepository,
			CustomerRepository customerRepository, ServiceRepository serviceRepository,
			AvailabilityService availabilityService, AppointmentRepository appointmentRepository,
			EmailService emailService) {
		this.vehicleRepository = vehicleRepository;
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.serviceRepository = serviceRepository;
		this.availabilityService = availabilityService;
		this.appointmentRepository = appointmentRepository;
		this.emailService = emailService;
	}

	// =========================
	// Crear cita, mail confirmación
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

		// 1) Guardar SIEMPRE primero
		AppointmentEntity saved = appointmentRepository.save(appointment);

		// 2) Intentar enviar email (best-effort). Si falla, NO bloquea la reserva.
		try {
			String to = saved.getCustomer().getUser().getEmail();
			String customerName = saved.getCustomer().getUser().getNombre();

			String serviceName = (saved.getService() != null) ? saved.getService().getNombre() : null;
			String plate = (saved.getVehicle() != null) ? saved.getVehicle().getMatricula() : null;

			String dtText = saved.getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

			var mailData = new AppointmentMailData(saved.getId(), customerName, serviceName, plate, dtText,
					saved.getEstado().name());

			emailService.sendAppointmentConfirmation(to, mailData);

		} catch (Exception ex) {
			// No imprimir stacktrace: aviso corto y seguimos
			log.warn("[APPOINTMENT] Cita {} creada, pero fallo enviando email de confirmación: {}", saved.getId(),
					ex.getMessage());
		}

		return saved.getId();
	}

	// =========================
	// Listado Mis Citas
	// =========================
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentEntity> listMyAppointments() {
		Long customerId = getCurrentCustomerIdOrThrow();
		return appointmentRepository.findByCustomer_IdCustomerOrderByInicioDesc(customerId);
	}

	// =========================
	// Mis Citas Detalle
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
	// Mis Citas Cancelar
	// =========================
	@Override
	public void cancelMyAppointment(Long appointmentId) {
		AppointmentEntity a = getMyAppointment(appointmentId);

		if (a.getEstado() == AppointmentStatus.FINALIZADA) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita finalizada");
		}
		if (a.getEstado() == AppointmentStatus.EN_CURSO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita en curso");
		}
		if (a.getEstado() == AppointmentStatus.CANCELADA) {
			return;
		}

		a.setEstado(AppointmentStatus.CANCELADA);
		appointmentRepository.save(a);
	}

	// =========================
	// helper: Cliente Logueado
	// =========================
	private Long getCurrentCustomerIdOrThrow() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
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
