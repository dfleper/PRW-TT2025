package es.prw.features.workorders.repository;

import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

    @Query("""
        select wo
        from WorkOrderEntity wo
        join fetch wo.appointment a
        left join fetch a.service s
        left join fetch a.vehicle v
        left join fetch a.customer c
        left join fetch c.user cu
        left join fetch a.employeeAsignado ea
        left join fetch ea.user eau
        where wo.id = :id
    """)
    Optional<WorkOrderEntity> findByIdWithView(@Param("id") Long id);

    @Query("""
        select wo
        from WorkOrderEntity wo
        join fetch wo.appointment a
        left join fetch a.service s
        left join fetch a.vehicle v
        left join fetch a.customer c
        left join fetch c.user cu
        left join fetch a.employeeAsignado ea
        left join fetch ea.user eau
        where a.id = :appointmentId
    """)
    Optional<WorkOrderEntity> findByAppointmentIdWithView(@Param("appointmentId") Long appointmentId);

    Optional<WorkOrderEntity> findByAppointment_Id(Long appointmentId);

    @Query("""
        select wo
        from WorkOrderEntity wo
        join fetch wo.appointment a
        left join fetch a.service s
        left join fetch a.vehicle v
        left join fetch a.customer c
        left join fetch c.user cu
        where (:status is null or wo.estado = :status)
          and (
                :q is null or trim(:q) = ''
                or lower(v.matricula) like lower(concat('%', :q, '%'))
                or lower(cu.email) like lower(concat('%', :q, '%'))
                or lower(cu.nombre) like lower(concat('%', :q, '%'))
                or lower(cu.apellidos) like lower(concat('%', :q, '%'))
              )
        order by wo.createdAt desc
    """)
    List<WorkOrderEntity> findListForBackoffice(
            @Param("status") WorkOrderStatus status,
            @Param("q") String q
    );

    // =========================================================
    // ✅ TAREA 22.6: MECÁNICO -> solo OTs NO cerradas y asignadas
    // =========================================================
    @Query("""
        select wo
        from WorkOrderEntity wo
        join fetch wo.appointment a
        left join fetch a.service s
        left join fetch a.vehicle v
        left join fetch a.customer c
        left join fetch c.user cu
        left join fetch a.employeeAsignado ea
        left join fetch ea.user eau
        where wo.estado <> :closed
          and ea.id = :employeeId
          and (
                :q is null or trim(:q) = ''
                or lower(v.matricula) like lower(concat('%', :q, '%'))
                or lower(cu.email) like lower(concat('%', :q, '%'))
                or lower(cu.nombre) like lower(concat('%', :q, '%'))
                or lower(cu.apellidos) like lower(concat('%', :q, '%'))
              )
        order by wo.createdAt desc
    """)
    List<WorkOrderEntity> findOpenAssignedToMechanic(
            @Param("employeeId") Long employeeId,
            @Param("closed") WorkOrderStatus closed,
            @Param("q") String q
    );
}
