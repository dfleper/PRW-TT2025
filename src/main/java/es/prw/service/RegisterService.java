package es.prw.service;

import es.prw.dto.RegisterRequest;
import es.prw.model.RoleEntity;
import es.prw.model.UserEntity;
import es.prw.repository.RoleRepository;
import es.prw.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerCliente(RegisterRequest req) {

        String email = req.getEmail().trim().toLowerCase();

        // Doble seguridad (además de @UniqueEmail)
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }

        RoleEntity roleCliente = roleRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE en BD"));

        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setNombre(req.getNombre().trim());
        u.setApellidos(req.getApellidos().trim());

        String tel = req.getTelefono();
        u.setTelefono((tel == null || tel.isBlank()) ? null : tel.trim());

        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setActivo(true);

        // Por si tus columnas son NOT NULL (aunque tengan default)
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());

        u.getRoles().add(roleCliente);

        try {
            userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            // Por si te cuelan un email duplicado por carrera/condición
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }
    }
}
