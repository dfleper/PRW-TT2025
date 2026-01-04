package es.prw.features.auth.service;

import es.prw.features.auth.dto.RegisterRequest;
import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.RoleRepository;
import es.prw.features.iam.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerClient(RegisterRequest req) {

        // 1) Verificar email no existente (normalizado)
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }

        // 2) Cargar rol CLIENTE (debe existir por Flyway seed)
        RoleEntity clienteRole = roleRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no existe en BD"));

        // 3) Crear usuario
        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setNombre(req.getNombre().trim());
        u.setApellidos(req.getApellidos().trim());

        String tel = req.getTelefono();
        u.setTelefono((tel == null || tel.isBlank()) ? null : tel.trim());

        u.setActivo(true);

        // created_at / updated_at son NOT NULL en tu BD
        LocalDateTime now = LocalDateTime.now();
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        // 4) Encriptar password con BCrypt
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        // 5) Asignar rol CLIENTE
        u.getRoles().add(clienteRole);

        // 6) Guardar en BD
        try {
            userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            // Si hay condición de carrera con el UNIQUE(email)
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }
    }
}
