package es.prw.service;

import es.prw.model.RoleEntity;
import es.prw.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public long countRoles() {
        return roleRepository.count();
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }
}
