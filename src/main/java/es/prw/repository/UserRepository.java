package es.prw.repository;

import es.prw.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
        SELECT u
        FROM UserEntity u
        LEFT JOIN FETCH u.roles r
        WHERE u.email = :email
    """)
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
}
