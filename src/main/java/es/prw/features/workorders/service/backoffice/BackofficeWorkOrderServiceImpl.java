package es.prw.features.workorders.service.backoffice;

import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;
import es.prw.features.workorders.service.backoffice.dto.WorkOrderListRow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class BackofficeWorkOrderServiceImpl implements BackofficeWorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    public BackofficeWorkOrderServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    public List<WorkOrderListRow> listWorkOrders(String status, String q) {

        WorkOrderStatus statusEnum = parseStatus(status);

        return workOrderRepository.findListForBackoffice(statusEnum, q)
                .stream()
                .map(wo -> {
                    var a = wo.getAppointment();
                    var v = a.getVehicle();
                    var s = a.getService();
                    var cu = a.getCustomer().getUser();

                    String customerName = joinName(cu.getNombre(), cu.getApellidos());

                    return new WorkOrderListRow(
                            wo.getId(),
                            toUiStatus(wo.getEstado()),              // OPEN / CLOSED
                            wo.getCreatedAt(),                       // fecha para tabla
                            wo.getClosedAt(),
                            customerName,
                            cu.getEmail(),
                            v.getMatricula(),
                            s.getNombre(),
                            a.getEstado() == null ? "" : a.getEstado().name()
                    );
                })
                .toList();
    }

    private WorkOrderStatus parseStatus(String status) {
        if (status == null) return null;

        String s = status.trim().toUpperCase(Locale.ROOT);
        if (s.isBlank() || s.equals("ALL")) return null;

        // UI: OPEN/CLOSED -> dominio: abierta/cerrada
        if (s.equals("OPEN")) return WorkOrderStatus.abierta;
        if (s.equals("CLOSED")) return WorkOrderStatus.cerrada;

        // Si llega algo raro, lo tratamos como ALL
        return null;
    }

    private String toUiStatus(WorkOrderStatus estado) {
        if (estado == null) return "OPEN";

        // ✅ Con default evitamos el error de exhaustividad del switch
        return switch (estado) {
            case abierta -> "OPEN";
            case cerrada -> "CLOSED";
            default -> estado.name().toUpperCase(Locale.ROOT); // por si aparecen más estados
        };
    }

    private String joinName(String nombre, String apellidos) {
        String n = (nombre == null) ? "" : nombre.trim();
        String a = (apellidos == null) ? "" : apellidos.trim();
        return (n + " " + a).trim();
    }
}
