package es.prw.features.appointments.repository;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;

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

    // ========= Disponibilidad por empleado =========
    @Query("""
        select count(a) > 0
        from AppointmentEntity a
        where a.employeeAsignado.id = :employeeId
          and a.estado <> :cancelada
          and a.inicio < :fin
          and a.fin > :inicio
    """)
    boolean existsOverlapForEmployee(
            @Param("employeeId") Long employeeId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("cancelada") AppointmentStatus cancelada
    );

    // ========= Detalle seguro con JOIN FETCH (evita LazyInitializationException) =========
    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        where a.id = :id
          and a.customer.idCustomer = :customerId
    """)
    Optional<AppointmentEntity> findDetailByIdAndCustomer(
            @Param("id") Long id,
            @Param("customerId") Long customerId
    );
}
