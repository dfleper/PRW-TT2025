package es.prw.features.parts.repository;

import es.prw.features.parts.domain.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<PartEntity, Long> {

    List<PartEntity> findAllByOrderByNombreAsc();
}
