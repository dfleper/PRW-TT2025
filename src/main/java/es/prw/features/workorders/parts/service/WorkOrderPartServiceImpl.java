package es.prw.features.workorders.parts.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.UserRepository;
import es.prw.features.parts.repository.PartRepository;
import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.parts.domain.WorkOrderPartEntity;
import es.prw.features.workorders.parts.dto.WorkOrderPartDto;
import es.prw.features.workorders.parts.repository.WorkOrderPartRepository;
import es.prw.features.workorders.repository.WorkOrderRepository;

@Service
@Transactional
public class WorkOrderPartServiceImpl implements WorkOrderPartService {

	private final WorkOrderPartRepository workOrderPartRepository;
	private final WorkOrderRepository workOrderRepository;
	private final PartRepository partRepository;
	private final UserRepository userRepository;

	public WorkOrderPartServiceImpl(WorkOrderPartRepository workOrderPartRepository,
			WorkOrderRepository workOrderRepository, PartRepository partRepository, UserRepository userRepository) {
		this.workOrderPartRepository = workOrderPartRepository;
		this.workOrderRepository = workOrderRepository;
		this.partRepository = partRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkOrderPartEntity> listByWorkOrder(Long workOrderId) {
		if (workOrderId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orden de trabajo no válida");

		workOrderRepository.findById(workOrderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

		return workOrderPartRepository.findByWorkOrder_IdOrderByIdAsc(workOrderId);
	}

	@Override
	public WorkOrderPartEntity addPart(Long workOrderId, WorkOrderPartDto dto) {

		WorkOrderEntity wo = requireNotClosedWorkOrder(workOrderId);

		WorkOrderPartEntity line = new WorkOrderPartEntity();
		line.setWorkOrder(wo);
		UserEntity actor = getCurrentUserOrNull();
		if (actor != null) {
			line.setCreatedByUser(actor);
			line.setUpdatedByUser(actor);
		}

		applyDto(line, dto);

		return workOrderPartRepository.save(line);
	}

	@Override
	public WorkOrderPartEntity updatePart(Long partLineId, WorkOrderPartDto dto) {

		if (partLineId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pieza usada no válida");
		}

		WorkOrderPartEntity existing = workOrderPartRepository.findWithWorkOrderById(partLineId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pieza usada no encontrada"));

		WorkOrderEntity wo = existing.getWorkOrder();
		if (wo == null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Pieza sin orden de trabajo asociada");
		}

		if (wo.getEstado() == WorkOrderStatus.cerrada) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "OT cerrada: no se permiten cambios");
		}

		applyDto(existing, dto);
		UserEntity actor = getCurrentUserOrNull();
		if (actor != null) {
			existing.setUpdatedByUser(actor);
		}

		return workOrderPartRepository.save(existing);
	}

	@Override
	public void deletePart(Long partLineId) {

		if (partLineId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pieza usada no válida");
		}

		WorkOrderPartEntity existing = workOrderPartRepository.findWithWorkOrderById(partLineId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pieza usada no encontrada"));

		WorkOrderEntity wo = existing.getWorkOrder();
		if (wo == null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Pieza sin orden de trabajo asociada");
		}

		if (wo.getEstado() == WorkOrderStatus.cerrada) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "OT cerrada: no se permiten cambios");
		}

		workOrderPartRepository.delete(existing);
	}

	private WorkOrderEntity requireNotClosedWorkOrder(Long workOrderId) {

		if (workOrderId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orden de trabajo no válida");
		}

		WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

		if (wo.getEstado() == WorkOrderStatus.cerrada) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "OT cerrada: no se permiten cambios");
		}

		return wo;
	}

	private void applyDto(WorkOrderPartEntity target, WorkOrderPartDto dto) {

		if (dto == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de pieza no válidos");
		if (dto.getPartId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una pieza");

		var part = partRepository.findById(dto.getPartId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pieza no válida"));

		BigDecimal qty = dto.getQuantity();
		if (qty == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad es obligatoria");
		if (qty.signum() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad debe ser > 0");
		if (qty.scale() > 2) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad admite como máximo 2 decimales");

		boolean allowsDecimal = Boolean.TRUE.equals(part.getAllowsDecimal());
		if (!allowsDecimal && qty.stripTrailingZeros().scale() > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta pieza no admite decimales");
		}

		BigDecimal unitPrice = part.getPrecioUnit();
		if (unitPrice == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "La pieza no tiene precio configurado");
		if (unitPrice.signum() < 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "La pieza tiene precio inválido en catálogo");

		target.setPartId(part.getId());
		target.setQuantity(qty);
		target.setUnitPrice(unitPrice);

		BigDecimal total = unitPrice.multiply(qty).setScale(2, RoundingMode.HALF_UP);
		target.setTotal(total);

		String notes = dto.getNotes();
		if (notes != null && notes.length() > 255) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notas máximo 255 caracteres");
		}
		target.setNotes(notes);
	}

	private UserEntity getCurrentUserOrNull() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return null;
		}

		String email = auth.getName();
		if (email == null || email.isBlank()) {
			return null;
		}

		return userRepository.findByEmail(email).orElse(null);
	}
}