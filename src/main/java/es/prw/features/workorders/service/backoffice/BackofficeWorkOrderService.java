package es.prw.features.workorders.service.backoffice;

import es.prw.features.workorders.service.backoffice.dto.WorkOrderListRow;
import java.util.List;

public interface BackofficeWorkOrderService {
    List<WorkOrderListRow> listWorkOrders(String status, String q);
}
