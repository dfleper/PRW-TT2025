package es.prw.features.workorders.service.backoffice;

import java.util.List;

import es.prw.features.workorders.service.backoffice.dto.WorkOrderListRow;

public interface BackofficeWorkOrderService {
	List<WorkOrderListRow> listWorkOrders(String status, String q);

	// Mecánico -> solo OTs abiertas asignadas
	List<WorkOrderListRow> listOpenAssignedTo(Long mechanicEmployeeId, String q);
}
