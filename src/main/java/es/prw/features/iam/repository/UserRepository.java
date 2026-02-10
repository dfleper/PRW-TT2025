package es.prw.features.iam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.prw.features.iam.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
        select distinct u
        from UserEntity u
        left join fetch u.roles r
        order by u.idUser asc
    """)
    List<UserEntity> findAllWithRoles();

    @Query("""
        SELECT u
        FROM UserEntity u
        LEFT JOIN FETCH u.roles r
        WHERE u.email = :email
    """)
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE UserEntity u
        SET u.lastLoginAt = :ts
        WHERE LOWER(u.email) = LOWER(:email)
    """)
    int updateLastLoginAtByEmail(@Param("email") String email, @Param("ts") LocalDateTime ts);
}
