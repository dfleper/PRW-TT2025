package es.prw.features.backoffice.agenda.web;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.backoffice.agenda.service.BackofficeAppointmentService;

@Controller
@RequestMapping("/backoffice")
public class BackofficeAppointmentController {

	private final BackofficeAppointmentService backofficeAppointmentService;

	public BackofficeAppointmentController(BackofficeAppointmentService backofficeAppointmentService) {
		this.backofficeAppointmentService = backofficeAppointmentService;
	}

	// Roles típicos de backoffice: RECEPCION, MECANICO, JEFE_TALLER, ADMIN
	// Ajusta los nombres a (ROLE_RECEPCION, etc.)
	@PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
	@GetMapping("/agenda")
	public String agenda(
			@RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

			@RequestParam(name = "status", required = false) AppointmentStatus status,

			Model model) {
		LocalDate targetDate = (date != null) ? date : LocalDate.now();

		var citas = backofficeAppointmentService.getAgenda(targetDate, Optional.ofNullable(status));

		model.addAttribute("date", targetDate);
		model.addAttribute("status", status); // Para mantener el filtro seleccionado
		model.addAttribute("statuses", AppointmentStatus.values()); // Para pintar el Select
		model.addAttribute("appointments", citas);

		return "backoffice/agenda/list";
	}
}
