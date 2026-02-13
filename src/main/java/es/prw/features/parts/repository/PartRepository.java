package es.prw.features.parts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.parts.domain.PartEntity;

public interface PartRepository extends JpaRepository<PartEntity, Long> {

	List<PartEntity> findAllByOrderByNombreAsc();
}
