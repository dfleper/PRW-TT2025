package es.prw.features.appointments.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;


public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    /**
     * Devuelve citas que solapen con el rango [requestedStart, requestedEnd).
     *
     * Regla de solape:
     * existingStart < requestedEnd AND existingEnd > requestedStart
     *
     * Ignora estados como 'cancelada' (y opcionalmente 'finalizada').
     */
    @Query("""
        select a
        from AppointmentEntity a
        where a.estado not in :ignoredStates
          and a.inicio < :requestedEnd
          and a.fin > :requestedStart
        """)
    List<AppointmentEntity> findOverlaps(
        @Param("requestedStart") LocalDateTime requestedStart,
        @Param("requestedEnd") LocalDateTime requestedEnd,
        @Param("ignoredStates") List<AppointmentStatus> ignoredStates
    );
}
