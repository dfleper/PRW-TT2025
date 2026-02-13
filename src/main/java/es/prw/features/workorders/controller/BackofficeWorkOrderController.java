package es.prw.features.workorders.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.prw.features.parts.repository.PartRepository;
import es.prw.features.workorders.parts.dto.WorkOrderPartDto;
import es.prw.features.workorders.parts.service.WorkOrderPartService;
import es.prw.features.workorders.service.WorkOrderService;
import es.prw.features.workorders.service.backoffice.BackofficeWorkOrderService;
import es.prw.features.workorders.service.backoffice.dto.WorkOrderListRow;

@Controller
@RequestMapping("/backoffice")
public class BackofficeWorkOrderController {

	private final WorkOrderService workOrderService;
	private final WorkOrderPartService workOrderPartService;
	private final PartRepository partRepository;

	// Nuevo: Servicio para el listado (lo completaremos en checks siguientes)
	private final BackofficeWorkOrderService backofficeWorkOrderService;

	public BackofficeWorkOrderController(WorkOrderService workOrderService, WorkOrderPartService workOrderPartService,
			PartRepository partRepository, BackofficeWorkOrderService backofficeWorkOrderService) {
		this.workOrderService = workOrderService;
		this.workOrderPartService = workOrderPartService;
		this.partRepository = partRepository;
		this.backofficeWorkOrderService = backofficeWorkOrderService;
	}

	// =========================================================
	// CHECK 1: LISTADO
	// GET /backoffice/workorders
	// =========================================================
	@GetMapping("/workorders")
	public String list(@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "q", required = false) String q, Model model) {
		List<WorkOrderListRow> rows;
		try {
			rows = backofficeWorkOrderService.listWorkOrders(status, q);
		} catch (Exception ex) {
			rows = java.util.Collections.emptyList();
			model.addAttribute("error",
					"No se pudo cargar el listado de órdenes de trabajo (servicio aún no implementado).");
		}

		model.addAttribute("rows", rows);
		model.addAttribute("status", (status == null || status.isBlank()) ? "ALL" : status);
		model.addAttribute("q", q == null ? "" : q);

		return "backoffice/workorders/list";
	}

	@PostMapping("/citas/{id}/workorder")
	public String openFromAppointment(@PathVariable("id") Long appointmentId) {
		var wo = workOrderService.getOrCreateForAppointment(appointmentId);
		return "redirect:/backoffice/workorders/" + wo.getId();
	}

	@GetMapping("/workorders/{id}")
	public String detail(@PathVariable("id") Long workOrderId, Model model) {
		var wo = workOrderService.getById(workOrderId);

		model.addAttribute("wo", wo);

		// Piezas usadas
		model.addAttribute("parts", workOrderPartService.listByWorkOrder(workOrderId));
		model.addAttribute("newPart", new WorkOrderPartDto());

		// Catálogo para el desplegable (id + nombre + sku + allowsDecimal)
		var catalog = partRepository.findAllByOrderByNombreAsc();
		model.addAttribute("catalogParts", catalog);

		// Map id -> sku
		Map<Long, String> partSku = new HashMap<>();
		for (var p : catalog) {
			partSku.put(p.getId(), p.getSku());
		}
		model.addAttribute("partSku", partSku);

		return "backoffice/workorders/detail";
	}

	@PostMapping("/workorders/{id}")
	public String saveNotes(@PathVariable("id") Long workOrderId,
			@RequestParam(name = "diagnostico", required = false) String diagnostico,
			@RequestParam(name = "observaciones", required = false) String observaciones, RedirectAttributes ra) {

		try {
			workOrderService.updateNotes(workOrderId, diagnostico, observaciones);
			ra.addFlashAttribute("ok", "Orden de trabajo guardada");
		} catch (ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		}

		return "redirect:/backoffice/workorders/" + workOrderId;
	}

	// Cerrar OT
	@PostMapping("/workorders/{id}/close")
	public String closeWorkOrder(@PathVariable("id") Long workOrderId, RedirectAttributes ra) {

		try {
			workOrderService.closeWorkOrder(workOrderId);
			ra.addFlashAttribute("ok", "Orden de trabajo cerrada correctamente");
		} catch (ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		} catch (Exception ex) {
			ra.addFlashAttribute("error", "Error inesperado al cerrar la orden de trabajo");
		}

		return "redirect:/backoffice/workorders/" + workOrderId;
	}
}
