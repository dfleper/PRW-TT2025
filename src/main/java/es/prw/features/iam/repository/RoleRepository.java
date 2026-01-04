package es.prw.features.iam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.iam.domain.RoleEntity;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Short> {
    Optional<RoleEntity> findByNombre(String nombre);
}
