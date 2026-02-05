package es.prw.features.backoffice.citas.web;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.backoffice.agenda.service.BackofficeAppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backoffice/citas")
public class BackofficeCitasController {

    private final BackofficeAppointmentService service;

    public BackofficeCitasController(BackofficeAppointmentService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var appt = service.getAppointmentDetail(id);
        model.addAttribute("appt", appt);

        // NUEVO: mecánicos para el select
        model.addAttribute("mechanics", service.findAllMechanics());

        return "backoffice/citas/detalle";
    }

    @PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam("newStatus") AppointmentStatus newStatus,
            org.springframework.web.servlet.mvc.support.RedirectAttributes ra
    ) {
        try {
            service.changeStatus(id, newStatus);
            ra.addFlashAttribute("ok", "Estado actualizado a " + newStatus);
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/backoffice/citas/" + id;
    }

    // NUEVO: asignar mecánico
    @PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
    @PostMapping("/{id}/asignar")
    public String asignarMecanico(
            @PathVariable Long id,
            @RequestParam("mechanicId") Long mechanicId,
            org.springframework.web.servlet.mvc.support.RedirectAttributes ra
    ) {
        try {
            service.assignMechanic(id, mechanicId);
            ra.addFlashAttribute("ok", "Mecánico asignado correctamente");
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/backoffice/citas/" + id;
    }
}
