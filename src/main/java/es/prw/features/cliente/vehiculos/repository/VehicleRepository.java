package es.prw.features.cliente.vehiculos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.cliente.vehiculos.domain.VehicleEntity;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

	List<VehicleEntity> findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(Long idCustomer);

	Optional<VehicleEntity> findByIdVehicleAndCustomer_IdCustomer(Long idVehicle, Long idCustomer);

	boolean existsByMatriculaIgnoreCase(String matricula);

	boolean existsByMatriculaIgnoreCaseAndIdVehicleNot(String matricula, Long idVehicle);

	// VIN único
	boolean existsByVinIgnoreCase(String vin);

	boolean existsByVinIgnoreCaseAndIdVehicleNot(String vin, Long idVehicle);
}
