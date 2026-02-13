package es.prw.features.workorders.service.backoffice.dto;

import java.time.LocalDateTime;

public record WorkOrderListRow(Long id, String otStatus, // OPEN/CLOSED (para UI)
		LocalDateTime createdAt, LocalDateTime closedAt, String customerName, String customerEmail, String plate,
		String serviceName, String appointmentStatus) {
}
