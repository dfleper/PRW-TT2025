package es.prw.features.iam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.iam.domain.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Short> {
	Optional<RoleEntity> findByNombre(String nombre);
}
