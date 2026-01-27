package es.prw.features.appointments.repository;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

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
}
