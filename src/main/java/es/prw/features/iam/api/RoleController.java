package es.prw.features.iam.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.service.RoleService;

@RestController
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	// Endpoint de prueba
	@GetMapping("/health/db")
	public String healthDb() {
		return "OK (roles.count=" + roleService.countRoles() + ")";
	}

	// Listar roles
	@GetMapping("/roles")
	public List<RoleEntity> listRoles() {
		return roleService.findAll();
	}
}
