package es.prw.features.workorders.service;

import es.prw.features.workorders.domain.WorkOrderEntity;

public interface WorkOrderService {

    WorkOrderEntity getOrCreateForAppointment(Long appointmentId);

    WorkOrderEntity getById(Long workOrderId);

    WorkOrderEntity updateNotes(Long workOrderId, String diagnostico, String observaciones);

    // ✅ Tarea 17: cerrar OT
    WorkOrderEntity closeWorkOrder(Long workOrderId);
}
