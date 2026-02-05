package es.prw.features.employees.repository;

import es.prw.features.employees.domain.EmployeeEntity;
import es.prw.features.employees.domain.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    // Mecánicos activos (y otros tipos si lo necesitas) -> SIN user (ojo en Thymeleaf)
    @Query("""
        select e
        from EmployeeEntity e
        where e.activo = true
          and e.tipo = :tipo
        order by e.id asc
    """)
    List<EmployeeEntity> findActivosByTipo(@Param("tipo") EmployeeType tipo);

    // ✅ Para THYMELEAF: incluye user con JOIN FETCH (evita LazyInitializationException)
    @Query("""
        select distinct e
        from EmployeeEntity e
        join fetch e.user u
        where e.activo = true
          and e.tipo = :tipo
        order by e.id asc
    """)
    List<EmployeeEntity> findActivosByTipoWithUser(@Param("tipo") EmployeeType tipo);

    // Atajo específico para mecánicos (opcional)
    default List<EmployeeEntity> findMecanicosActivos() {
        return findActivosByTipo(EmployeeType.MECANICO);
    }

    // ✅ Atajo para mecánicos (con user cargado)
    default List<EmployeeEntity> findMecanicosActivosWithUser() {
        return findActivosByTipoWithUser(EmployeeType.MECANICO);
    }
}
