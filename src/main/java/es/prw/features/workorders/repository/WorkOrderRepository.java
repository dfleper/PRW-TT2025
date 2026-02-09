package es.prw.features.workorders.repository;

import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // =========================================================
    // ✅ CHECK 2: LISTADO + FILTRO ESTADO + BUSQUEDA (q)
    // - status: null => ALL
    // - q: null/blank => sin filtro
    // Busca por:
    //   - matrícula (vehicle.matricula)
    //   - cliente (user.nombre / user.apellidos / user.email)
    // Orden: más reciente primero
    // =========================================================
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
}
