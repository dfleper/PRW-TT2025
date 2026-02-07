package es.prw.features.workorders.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final AppointmentRepository appointmentRepository;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository,
                                AppointmentRepository appointmentRepository) {
        this.workOrderRepository = workOrderRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public WorkOrderEntity getOrCreateForAppointment(Long appointmentId) {

        // ✅ Traer con "vista" (appointment + service + vehicle + customer.user + employeeAsignado.user)
        var existing = workOrderRepository.findByAppointmentIdWithView(appointmentId);
        if (existing.isPresent()) return existing.get();

        AppointmentEntity appt = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        WorkOrderEntity wo = new WorkOrderEntity();
        wo.setAppointment(appt);
        wo.setEstado(WorkOrderStatus.abierta);

        try {
            WorkOrderEntity saved = workOrderRepository.save(wo);

            // ✅ Releer con fetch join "vista" para devolver completamente inicializado
            return workOrderRepository.findByIdWithView(saved.getId()).orElse(saved);

        } catch (DataIntegrityViolationException ex) {
            // Si dos threads crean a la vez, nos quedamos con la existente
            return workOrderRepository.findByAppointmentIdWithView(appointmentId)
                .orElseThrow(() -> ex);
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

        // ✅ Regla mínima: OT cerrada = NO se puede editar
        if (wo.getEstado() == WorkOrderStatus.cerrada) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La orden de trabajo está cerrada y no permite cambios"
            );
        }

        wo.setDiagnostico(diagnostico);
        wo.setObservaciones(observaciones);

        return workOrderRepository.save(wo);
    }

    @Override
    public WorkOrderEntity closeWorkOrder(Long workOrderId) {

        WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

        if (wo.getEstado() == WorkOrderStatus.cerrada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden de trabajo ya está cerrada");
        }

        // ✅ Cierre
        wo.setEstado(WorkOrderStatus.cerrada);

        // Si ya viniera informada por lo que sea, no la pisamos.
        if (wo.getClosedAt() == null) {
            wo.setClosedAt(LocalDateTime.now());
        }

        return workOrderRepository.save(wo);
    }
}
