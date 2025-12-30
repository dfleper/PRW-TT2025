package es.prw.repository;

import es.prw.model.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    List<VehicleEntity> findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(Long idCustomer);

    Optional<VehicleEntity> findByIdVehicleAndCustomer_IdCustomer(Long idVehicle, Long idCustomer);

    boolean existsByMatriculaIgnoreCase(String matricula);

    boolean existsByMatriculaIgnoreCaseAndIdVehicleNot(String matricula, Long idVehicle);
}
