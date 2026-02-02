package es.prw.features.appointments.repository;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.employees.domain.EmployeeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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

    // ========= Listado de citas del cliente (con JOIN FETCH para evitar LazyInitializationException) =========
    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        where a.customer.idCustomer = :customerId
        order by a.inicio desc
    """)
    List<AppointmentEntity> findListByCustomerWithJoinsDesc(@Param("customerId") Long customerId);

    // (si luego quieres ASC)
    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        where a.customer.idCustomer = :customerId
        order by a.inicio asc
    """)
    List<AppointmentEntity> findListByCustomerWithJoinsAsc(@Param("customerId") Long customerId);

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

    // ========= Derivados existentes =========
    List<AppointmentEntity> findByCustomer_IdCustomerOrderByInicioDesc(Long customerId);
    List<AppointmentEntity> findByCustomer_IdCustomerOrderByInicioAsc(Long customerId);

    // ========= BACKOFFICE: agenda diaria (JOIN FETCH completo para Thymeleaf) =========
    // Rango [start, end) = [00:00 del día, 00:00 del día siguiente)
    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        join fetch a.customer c
        join fetch c.user u
        where a.inicio >= :start and a.inicio < :end
        order by a.inicio asc
    """)
    List<AppointmentEntity> findAgendaByDay(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        select a
        from AppointmentEntity a
        join fetch a.service
        join fetch a.vehicle
        join fetch a.customer c
        join fetch c.user u
        where a.inicio >= :start and a.inicio < :end
          and a.estado = :estado
        order by a.inicio asc
    """)
    List<AppointmentEntity> findAgendaByDayAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("estado") AppointmentStatus estado
    );

    // ========= BACKOFFICE: agenda diaria (derivados - opcional) =========
    // Si algún día quieres no usar joins (no recomendado para Thymeleaf si accedes a relaciones)
    List<AppointmentEntity> findByInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    List<AppointmentEntity> findByInicioGreaterThanEqualAndInicioLessThanAndEstadoOrderByInicioAsc(
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus estado
    );
}
