package es.prw.features.backoffice.citas.web;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.backoffice.agenda.service.BackofficeAppointmentService;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backoffice/citas")
public class BackofficeCitasController {

    private final BackofficeAppointmentService service;
    private final WorkOrderRepository workOrderRepository;

    public BackofficeCitasController(BackofficeAppointmentService service,
                                     WorkOrderRepository workOrderRepository) {
        this.service = service;
        this.workOrderRepository = workOrderRepository;
    }

    @PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var appt = service.getAppointmentDetail(id);
        model.addAttribute("appt", appt);

        // mecánicos para el select
        model.addAttribute("mechanics", service.findAllMechanics());

        // ✅ OT asociada (si existe) para UI/validaciones visuales
        var woOpt = workOrderRepository.findByAppointment_Id(id);
        model.addAttribute("wo", woOpt.orElse(null));

        boolean canFinalize =
                appt.getEstado() == AppointmentStatus.EN_CURSO
                        && woOpt.isPresent()
                        && woOpt.get().getEstado() == WorkOrderStatus.cerrada;

        model.addAttribute("canFinalize", canFinalize);

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

    // ✅ Endpoint “limpio” para finalizar (recomendado)
    @PreAuthorize("hasAnyRole('RECEPCION','MECANICO','JEFE_TALLER','ADMIN')")
    @PostMapping("/{id}/finalize")
    public String finalizarCita(
            @PathVariable Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes ra
    ) {
        try {
            service.changeStatus(id, AppointmentStatus.FINALIZADA);
            ra.addFlashAttribute("ok", "Cita finalizada correctamente");
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/backoffice/citas/" + id;
    }

    // asignar mecánico
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
