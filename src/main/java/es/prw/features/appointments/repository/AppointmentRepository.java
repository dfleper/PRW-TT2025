package es.prw.features.appointments.repository;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.employees.domain.EmployeeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    // ========= Disponibilidad global (cliente) =========
    @Query("""
        select count(a) > 0
        from AppointmentEntity a
        where a.estado <> :cancelada
          and a.inicio < :fin
          and a.fin > :inicio
    """)
    boolean existsOverlap(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("cancelada") AppointmentStatus cancelada
    );

    // ========= Disponibilidad por empleado (robusto: compara entidad) =========
    @Query("""
        select count(a) > 0
        from AppointmentEntity a
        where a.employeeAsignado = :employee
          and a.estado <> :cancelada
          and a.inicio < :fin
          and a.fin > :inicio
    """)
    boolean existsOverlapForEmployee(
            @Param("employee") EmployeeEntity employee,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("cancelada") AppointmentStatus cancelada
    );

    /*
    // ========= Alternativa si INSISTES en employeeId (requiere saber el nombre real del ID en EmployeeEntity) =========
    // Si en EmployeeEntity el getter es getIdEmployee() -> propiedad "idEmployee"
    @Query("""
        select count(a) > 0
        from AppointmentEntity a
        where a.employeeAsignado.idEmployee = :employeeId
          and a.estado <> :cancelada
          and a.inicio < :fin
          and a.fin > :inicio
    """)
    boolean existsOverlapForEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("cancelada") AppointmentStatus cancelada
    );
    */

    // ========= Detalle seguro con JOIN FETCH (evita LazyInitializationException) =========
    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        join fetch a.customer
        where a.id = :id
          and a.customer.idCustomer = :customerId
    """)
    Optional<AppointmentEntity> findDetailByIdAndCustomer(
            @Param("id") Long id,
            @Param("customerId") Long customerId
    );
}
