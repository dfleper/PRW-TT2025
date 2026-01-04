package es.prw.features.catalog.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import es.prw.features.catalog.domain.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByActivoTrue(Sort sort);

    @Query("""
        select s
        from ServiceEntity s
        where s.activo = true
          and (
            lower(s.nombre) like lower(concat('%', :q, '%'))
            or lower(s.codigo) like lower(concat('%', :q, '%'))
          )
        """)
    List<ServiceEntity> searchActive(@Param("q") String q, Sort sort);
}
