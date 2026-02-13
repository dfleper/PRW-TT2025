package es.prw.features.catalog.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;

@Service
public class ServiceCatalogService {

	private final ServiceRepository repo;

	public ServiceCatalogService(ServiceRepository repo) {
		this.repo = repo;
	}

	public List<ServiceEntity> listActive(String q, String sort) {
		Sort s = resolveSort(sort);
		if (!StringUtils.hasText(q)) {
			return repo.findByActivoTrue(s);
		}
		return repo.searchActive(q.trim(), s);
	}

	private Sort resolveSort(String sort) {
		// Valores permitidos: nombre, precio, duración
		if ("precio".equalsIgnoreCase(sort)) {
			return Sort.by(Sort.Direction.ASC, "precioBase");
		}
		if ("duracion".equalsIgnoreCase(sort)) {
			return Sort.by(Sort.Direction.ASC, "minutosEstimados");
		}
		return Sort.by(Sort.Direction.ASC, "nombre");
	}
}
