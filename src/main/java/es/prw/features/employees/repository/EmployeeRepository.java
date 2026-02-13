package es.prw.features.employees.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.prw.features.employees.domain.EmployeeEntity;
import es.prw.features.employees.domain.EmployeeType;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

	@Query("""
			    select e
			    from EmployeeEntity e
			    where e.activo = true
			      and e.tipo = :tipo
			    order by e.id asc
			""")
	List<EmployeeEntity> findActivosByTipo(@Param("tipo") EmployeeType tipo);

	@Query("""
			    select distinct e
			    from EmployeeEntity e
			    join fetch e.user u
			    where e.activo = true
			      and e.tipo = :tipo
			    order by e.id asc
			""")
	List<EmployeeEntity> findActivosByTipoWithUser(@Param("tipo") EmployeeType tipo);

	default List<EmployeeEntity> findMecanicosActivos() {
		return findActivosByTipo(EmployeeType.MECANICO);
	}

	default List<EmployeeEntity> findMecanicosActivosWithUser() {
		return findActivosByTipoWithUser(EmployeeType.MECANICO);
	}

	// =========================================================
	// Identificar al mecánico logueado por email
	// =========================================================
	@Query("""
			    select e
			    from EmployeeEntity e
			    join fetch e.user u
			    where lower(u.email) = lower(:email)
			""")
	Optional<EmployeeEntity> findByUserEmailWithUser(@Param("email") String email);
}
