package es.prw.features.workorders.parts.repository;

import es.prw.features.cliente.historial.dto.WorkOrderPartView;
import es.prw.features.workorders.parts.domain.WorkOrderPartEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPartEntity, Long> {

    // ========= (1) Métodos que tu WorkOrderPartServiceImpl está usando =========

    // Lista de piezas por OT (ordenadas)
    List<WorkOrderPartEntity> findByWorkOrder_IdOrderByIdAsc(Long workOrderId);

    // Traer una línea con su WorkOrder cargada (evita Lazy en Service)
    @EntityGraph(attributePaths = {"workOrder"})
    Optional<WorkOrderPartEntity> findWithWorkOrderById(Long id);

    // ========= (2) Para el historial del cliente (pintar nombre/sku) =========

    @Query("""
        select new es.prw.features.cliente.historial.dto.WorkOrderPartView(
            w.id,
            w.partId,
            p.sku,
            p.nombre,
            w.quantity,
            w.unitPrice,
            w.total,
            w.notes
        )
        from WorkOrderPartEntity w
        join es.prw.features.parts.domain.PartEntity p
          on p.id = w.partId
        where w.workOrder.id = :workOrderId
        order by w.id asc
    """)
    List<WorkOrderPartView> findViewByWorkOrderId(@Param("workOrderId") Long workOrderId);
}
