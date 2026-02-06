package es.prw.features.workorders.repository;

import es.prw.features.workorders.domain.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

    // ✅ Para pintar la pantalla sin LazyInitialization:
    // WorkOrder -> Appointment -> (service, vehicle, customer.user, employeeAsignado.user)
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

    // (por si lo usas en otros sitios)
    Optional<WorkOrderEntity> findByAppointment_Id(Long appointmentId);
}
