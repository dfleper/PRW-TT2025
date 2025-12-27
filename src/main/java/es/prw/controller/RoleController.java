package es.prw.controller;

import es.prw.model.RoleEntity;
import es.prw.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
