package es.prw.features.workorders.controller;

import es.prw.features.workorders.service.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/backoffice")
public class BackofficeWorkOrderController {

    private final WorkOrderService workOrderService;

    public BackofficeWorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
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
        return "backoffice/workorders/detail";
    }

    @PostMapping("/workorders/{id}")
    public String saveNotes(@PathVariable("id") Long workOrderId,
                            @RequestParam(name = "diagnostico", required = false) String diagnostico,
                            @RequestParam(name = "observaciones", required = false) String observaciones,
                            RedirectAttributes ra) {

        workOrderService.updateNotes(workOrderId, diagnostico, observaciones);
        ra.addFlashAttribute("ok", "Orden de trabajo guardada");
        return "redirect:/backoffice/workorders/" + workOrderId;
    }
}
