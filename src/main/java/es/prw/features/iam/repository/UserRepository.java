package es.prw.features.iam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.prw.features.iam.domain.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
        SELECT u
        FROM UserEntity u
        LEFT JOIN FETCH u.roles r
        WHERE u.email = :email
    """)
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserEntity> findByEmail(String email);

}
