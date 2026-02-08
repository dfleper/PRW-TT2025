package es.prw.features.cliente.historial.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.cliente.historial.dto.WorkOrderPartView;

import java.util.List;

public interface HistoryService {

    List<AppointmentEntity> getVehicleHistory(Long vehicleId, Long customerId);

    HistoryDetail getHistoryDetail(Long appointmentId, Long customerId);

    record HistoryDetail(
            AppointmentEntity appointment,
            List<WorkOrderPartView> parts
    ) {}
}
