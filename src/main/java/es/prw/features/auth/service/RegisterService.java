package es.prw.features.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.prw.features.auth.dto.RegisterRequest;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.RoleRepository;
import es.prw.features.iam.repository.UserRepository;

@Service
public class RegisterService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	public RegisterService(UserRepository userRepository, RoleRepository roleRepository,
			CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.customerRepository = customerRepository;
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

		// NO setear auditoría: lo gestiona la BD (DEFAULT CURRENT_TIMESTAMP)
		// u.setCreatedAt(...)
		// u.setUpdatedAt(...)

		u.getRoles().add(roleCliente);

		try {
			UserEntity savedUser = userRepository.save(u);

			// Crear perfil customer asociado
			// Evita 403 futuros en módulos que dependen de CustomerEntity (vehículos,
			// citas, etc.)
			CustomerEntity c = new CustomerEntity();
			c.setUser(savedUser);
			customerRepository.save(c);

		} catch (DataIntegrityViolationException ex) {
			// Por si cuelan un email duplicado por carrera/condición
			throw new IllegalArgumentException("EMAIL_EXISTS");
		}
	}
}
