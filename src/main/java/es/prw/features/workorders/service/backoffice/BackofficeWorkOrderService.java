package es.prw.features.workorders.service.backoffice;

import es.prw.features.workorders.service.backoffice.dto.WorkOrderListRow;

import java.util.List;

public interface BackofficeWorkOrderService {
    List<WorkOrderListRow> listWorkOrders(String status, String q);

    // ✅ TAREA 22.6: mecánico -> solo OTs abiertas asignadas
    List<WorkOrderListRow> listOpenAssignedTo(Long mechanicEmployeeId, String q);
}
