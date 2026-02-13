package es.prw.features.cliente.historial.service;

import java.util.List;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.cliente.historial.dto.WorkOrderPartView;

public interface HistoryService {

	List<AppointmentEntity> getVehicleHistory(Long vehicleId, Long customerId);

	HistoryDetail getHistoryDetail(Long appointmentId, Long customerId);

	record HistoryDetail(AppointmentEntity appointment, List<WorkOrderPartView> parts) {
	}
}
