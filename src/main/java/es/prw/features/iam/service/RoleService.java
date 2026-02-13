package es.prw.features.iam.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.prw.features.iam.domain.RoleEntity;
import es.prw.features.iam.repository.RoleRepository;

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
