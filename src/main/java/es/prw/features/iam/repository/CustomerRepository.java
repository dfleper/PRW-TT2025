package es.prw.features.iam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.iam.domain.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
	Optional<CustomerEntity> findByUser_IdUser(Long idUser);
}
