package es.prw.features.workorders.parts.repository;

import es.prw.features.workorders.parts.domain.WorkOrderPartEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPartEntity, Long> {

    List<WorkOrderPartEntity> findByWorkOrderIdOrderByIdAsc(Long workOrderId);

    @EntityGraph(attributePaths = "workOrder")
    Optional<WorkOrderPartEntity> findWithWorkOrderById(Long id);
}
