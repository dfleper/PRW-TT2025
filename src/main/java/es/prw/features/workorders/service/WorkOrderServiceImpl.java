package es.prw.features.workorders.service;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.UserRepository;
import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;

@Service
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

	private final WorkOrderRepository workOrderRepository;
	private final AppointmentRepository appointmentRepository;
	private final UserRepository userRepository;

	public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository, AppointmentRepository appointmentRepository,
			UserRepository userRepository) {
		this.workOrderRepository = workOrderRepository;
		this.appointmentRepository = appointmentRepository;
		this.userRepository = userRepository;
	}

	@Override
	public WorkOrderEntity getOrCreateForAppointment(Long appointmentId) {

		var existing = workOrderRepository.findByAppointmentIdWithView(appointmentId);
		if (existing.isPresent()) return existing.get();

		AppointmentEntity appt = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

		WorkOrderEntity wo = new WorkOrderEntity();
		wo.setAppointment(appt);
		wo.setEstado(WorkOrderStatus.abierta);
		UserEntity actor = getCurrentUserOrNull();
		if (actor != null) {
			wo.setCreatedByUser(actor);
			wo.setUpdatedByUser(actor);
		}

		try {
			WorkOrderEntity saved = workOrderRepository.save(wo);
			return workOrderRepository.findByIdWithView(saved.getId()).orElse(saved);

		} catch (DataIntegrityViolationException ex) {
			return workOrderRepository.findByAppointmentIdWithView(appointmentId).orElseThrow(() -> ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public WorkOrderEntity getById(Long workOrderId) {
		return workOrderRepository.findByIdWithView(workOrderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));
	}

	@Override
	public WorkOrderEntity updateNotes(Long workOrderId, String diagnostico, String observaciones) {

		WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

		if (wo.getEstado() == WorkOrderStatus.cerrada) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"La orden de trabajo está cerrada y no permite cambios");
		}

		wo.setDiagnostico(diagnostico);
		wo.setObservaciones(observaciones);
		UserEntity actor = getCurrentUserOrNull();
		if (actor != null) {
			wo.setUpdatedByUser(actor);
		}

		return workOrderRepository.save(wo);
	}

	@Override
	public WorkOrderEntity closeWorkOrder(Long workOrderId) {

		WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

		if (wo.getEstado() == WorkOrderStatus.cerrada) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden de trabajo ya está cerrada");
		}

		wo.setEstado(WorkOrderStatus.cerrada);

		if (wo.getClosedAt() == null) {
			wo.setClosedAt(LocalDateTime.now());
		}

		UserEntity actor = getCurrentUserOrNull();
		if (actor != null) {
			wo.setUpdatedByUser(actor);
		}

		return workOrderRepository.save(wo);
	}

	private UserEntity getCurrentUserOrNull() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return null;
		}

		String email = auth.getName();
		if (email == null || email.isBlank()) {
			return null;
		}

		return userRepository.findByEmail(email).orElse(null);
	}
}