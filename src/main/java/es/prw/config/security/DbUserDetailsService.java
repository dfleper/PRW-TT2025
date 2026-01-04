package es.prw.config.security;

import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleEntity role : user.getRoles()) {
            String name = role.getNombre(); // ej: ADMIN, CLIENTE...
            authorities.add(new SimpleGrantedAuthority(name));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + name)); // compatibilidad
        }

        boolean enabled = Boolean.TRUE.equals(user.getActivo());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash()) // BCrypt en BD
                .authorities(authorities)
                .disabled(!enabled)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
