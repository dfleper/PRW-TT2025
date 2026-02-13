package es.prw.features.admin.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.RoleRepository;
import es.prw.features.iam.repository.UserRepository;

@Service
@Transactional
public class AdminUserService {

	private static final String ROLE_ADMIN = "ADMIN";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public AdminUserService(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Transactional(readOnly = true)
	public List<UserEntity> listUsersWithRoles() {
		return userRepository.findAllWithRoles();
	}

	@Transactional(readOnly = true)
	public List<RoleEntity> listAllRoles() {
		return roleRepository.findAll();
	}

	public void updateUserRoles(Long userId, Set<String> roleNames, Authentication auth) {

		if (roleNames == null || roleNames.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener al menos un rol.");
		}

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

		// Regla: el admin logueado no puede quitarse su propio rol ADMIN
		if (isEditingSelf(user, auth) && !roleNames.contains(ROLE_ADMIN)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes quitarte tu propio rol ADMIN.");
		}

		Set<RoleEntity> newRoles = new HashSet<>();
		for (String rn : roleNames) {
			String normalized = rn != null ? rn.trim().toUpperCase() : "";
			if (normalized.isEmpty())
				continue;

			RoleEntity role = roleRepository.findByNombre(normalized).orElseThrow(
					() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no existe: " + normalized));
			newRoles.add(role);
		}

		if (newRoles.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener al menos un rol.");
		}

		user.getRoles().clear();
		user.getRoles().addAll(newRoles);
		userRepository.save(user);
	}

	public void toggleActive(Long userId, Authentication auth) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

		boolean nextActive = !Boolean.TRUE.equals(user.getActivo());

		// Regla: el admin logueado no puede desactivarse a sí mismo
		if (isEditingSelf(user, auth) && !nextActive) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes desactivarte a ti mismo.");
		}

		user.setActivo(nextActive);
		userRepository.save(user);
	}

	private boolean isEditingSelf(UserEntity target, Authentication auth) {
		if (auth == null || auth.getName() == null)
			return false;
		return Objects.equals(auth.getName().toLowerCase(), target.getEmail().toLowerCase());
	}
}
