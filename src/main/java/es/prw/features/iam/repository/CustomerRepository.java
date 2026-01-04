package es.prw.features.iam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.prw.features.iam.domain.CustomerEntity;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByUser_IdUser(Long idUser);
}
