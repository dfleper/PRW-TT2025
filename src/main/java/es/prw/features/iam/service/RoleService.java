package es.prw.features.iam.service;

import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.repository.RoleRepository;

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
