package es.prw.features.backoffice.citas.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.backoffice.agenda.service.BackofficeAppointmentService;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;

@Controller
@RequestMapping("/backoffice/citas")
public class BackofficeCitasController {

	private final BackofficeAppointmentService service;
	private final WorkOrderRepository workOrderRepository;

	public BackofficeCitasController(BackofficeAppointmentService service, WorkOrderRepository workOrderRepository) {
		this.service = service;
		this.workOrderRepository = workOrderRepository;
	}

	@PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
	@GetMapping("/{id}")
	public String detalle(@PathVariable Long id, Model model) {
		var appt = service.getAppointmentDetail(id);
		model.addAttribute("appt", appt);

		// Mecánicos para el Select
		model.addAttribute("mechanics", service.findAllMechanics());

		// OT asociada (si existe) para UI/validaciones visuales
		var woOpt = workOrderRepository.findByAppointment_Id(id);
		model.addAttribute("wo", woOpt.orElse(null));

		boolean canFinalize = appt.getEstado() == AppointmentStatus.EN_CURSO && woOpt.isPresent()
				&& woOpt.get().getEstado() == WorkOrderStatus.cerrada;

		model.addAttribute("canFinalize", canFinalize);

		return "backoffice/citas/detalle";
	}

	@PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
	@PostMapping("/{id}/estado")
	public String cambiarEstado(@PathVariable Long id, @RequestParam("newStatus") AppointmentStatus newStatus,
			Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
		if (hasMechanicRole(authentication)) {
			ra.addFlashAttribute("error", "No tienes permisos para cambiar el estado de la cita.");
			return "redirect:/backoffice/citas/" + id;
		}
		try {
			service.changeStatus(id, newStatus);
			ra.addFlashAttribute("ok", "Estado actualizado a " + formatStatus(newStatus));
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/backoffice/citas/" + id;
	}

	// Endpoint “limpio” para finalizar
	@PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
	@PostMapping("/{id}/finalize")
	public String finalizarCita(@PathVariable Long id, Authentication authentication,
			org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
		if (hasMechanicRole(authentication)) {
			ra.addFlashAttribute("error", "No tienes permisos para finalizar la cita.");
			return "redirect:/backoffice/citas/" + id;
		}
		try {
			service.changeStatus(id, AppointmentStatus.FINALIZADA);
			ra.addFlashAttribute("ok", "Cita finalizada correctamente");
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/backoffice/citas/" + id;
	}

	// Asignar Mecánico
	@PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
	@PostMapping("/{id}/asignar")
	public String asignarMecanico(@PathVariable Long id, @RequestParam("mechanicId") Long mechanicId,
			Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
		if (hasMechanicRole(authentication)) {
			ra.addFlashAttribute("error", "No tienes permisos para asignar mecánico.");
			return "redirect:/backoffice/citas/" + id;
		}
		try {
			service.assignMechanic(id, mechanicId);
			ra.addFlashAttribute("ok", "Mecánico asignado correctamente");
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:/backoffice/citas/" + id;
	}

	private boolean hasMechanicRole(Authentication authentication) {
		return authentication != null && authentication.getAuthorities().stream().anyMatch(
				a -> "MECANICO".equals(a.getAuthority()) || "ROLE_MECANICO".equals(a.getAuthority()));
	}

	private String formatStatus(AppointmentStatus status) {
		return status == null ? "" : status.name().replace('_', ' ');
	}
}