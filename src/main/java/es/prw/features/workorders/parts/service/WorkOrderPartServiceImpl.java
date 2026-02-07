package es.prw.features.workorders.parts.service;

import es.prw.features.parts.repository.PartRepository;
import es.prw.features.workorders.domain.WorkOrderEntity;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;
import es.prw.features.workorders.parts.domain.WorkOrderPartEntity;
import es.prw.features.workorders.parts.dto.WorkOrderPartDto;
import es.prw.features.workorders.parts.repository.WorkOrderPartRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class WorkOrderPartServiceImpl implements WorkOrderPartService {

    private final WorkOrderPartRepository workOrderPartRepository;
    private final WorkOrderRepository workOrderRepository;
    private final PartRepository partRepository;

    public WorkOrderPartServiceImpl(
            WorkOrderPartRepository workOrderPartRepository,
            WorkOrderRepository workOrderRepository,
            PartRepository partRepository
    ) {
        this.workOrderPartRepository = workOrderPartRepository;
        this.workOrderRepository = workOrderRepository;
        this.partRepository = partRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrderPartEntity> listByWorkOrder(Long workOrderId) {

        workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

        return workOrderPartRepository.findByWorkOrderIdOrderByIdAsc(workOrderId);
    }

    @Override
    public WorkOrderPartEntity addPart(Long workOrderId, WorkOrderPartDto dto) {

        WorkOrderEntity wo = requireOpenWorkOrder(workOrderId);

        WorkOrderPartEntity line = new WorkOrderPartEntity();
        line.setWorkOrder(wo);

        applyDto(line, dto);

        return workOrderPartRepository.save(line);
    }

    @Override
    public WorkOrderPartEntity updatePart(Long partLineId, WorkOrderPartDto dto) {

        WorkOrderPartEntity existing = workOrderPartRepository.findWithWorkOrderById(partLineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pieza usada no encontrada"));

        if (existing.getWorkOrder() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pieza sin orden de trabajo asociada");
        }
        if (existing.getWorkOrder().getEstado() != WorkOrderStatus.abierta) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "OT no abierta: no se permiten cambios");
        }

        applyDto(existing, dto);

        return workOrderPartRepository.save(existing);
    }

    @Override
    public void deletePart(Long partLineId) {

        WorkOrderPartEntity existing = workOrderPartRepository.findWithWorkOrderById(partLineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pieza usada no encontrada"));

        if (existing.getWorkOrder() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pieza sin orden de trabajo asociada");
        }
        if (existing.getWorkOrder().getEstado() != WorkOrderStatus.abierta) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "OT no abierta: no se permiten cambios");
        }

        workOrderPartRepository.delete(existing);
    }

    // ===================== Helpers =====================

    private WorkOrderEntity requireOpenWorkOrder(Long workOrderId) {

        WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de trabajo no encontrada"));

        if (wo.getEstado() != WorkOrderStatus.abierta) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "OT no abierta: no se permiten cambios");
        }
        return wo;
    }

    /**
     * applyDto:
     * - Valida entrada
     * - Lee precio desde catálogo (parts.precio_unit)
     * - Valida decimales solo si parts.allows_decimal = 1
     * - Copia unitPrice (snapshot) y calcula total
     * - Aplica quantity y notes
     */
    private void applyDto(WorkOrderPartEntity target, WorkOrderPartDto dto) {

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de pieza no válidos");
        }

        if (dto.getPartId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una pieza");
        }

        var part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pieza no válida"));

        // ---- cantidad (BigDecimal para permitir 3.5)
        BigDecimal qty = dto.getQuantity();
        if (qty == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad es obligatoria");
        }
        if (qty.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad debe ser > 0");
        }
        // limitamos a 2 decimales como máximo
        if (qty.scale() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad admite como máximo 2 decimales");
        }

        boolean allowsDecimal = Boolean.TRUE.equals(part.getAllowsDecimal());
        // Si NO permite decimales => debe ser entero exacto
        if (!allowsDecimal && qty.stripTrailingZeros().scale() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta pieza no admite decimales");
        }

        // ---- precio desde catálogo (snapshot)
        BigDecimal unitPrice = part.getPrecioUnit();
        if (unitPrice == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pieza no tiene precio configurado");
        }
        if (unitPrice.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pieza tiene precio inválido en catálogo");
        }

        // ---- aplicar
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
}
