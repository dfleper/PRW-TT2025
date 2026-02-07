package es.prw.features.workorders.controller;

import es.prw.features.parts.repository.PartRepository;
import es.prw.features.workorders.parts.dto.WorkOrderPartDto;
import es.prw.features.workorders.parts.service.WorkOrderPartService;
import es.prw.features.workorders.service.WorkOrderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/backoffice")
public class BackofficeWorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderPartService workOrderPartService;
    private final PartRepository partRepository;

    public BackofficeWorkOrderController(
            WorkOrderService workOrderService,
            WorkOrderPartService workOrderPartService,
            PartRepository partRepository
    ) {
        this.workOrderService = workOrderService;
        this.workOrderPartService = workOrderPartService;
        this.partRepository = partRepository;
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

        // ✅ piezas usadas
        model.addAttribute("parts", workOrderPartService.listByWorkOrder(workOrderId));
        model.addAttribute("newPart", new WorkOrderPartDto());

        // ✅ catálogo para el desplegable (id + nombre + sku + allowsDecimal)
        var catalog = partRepository.findAllByOrderByNombreAsc();
        model.addAttribute("catalogParts", catalog);

        // ✅ Map id -> sku (para pintar SKU en tabla sin lambdas)
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
                            @RequestParam(name = "observaciones", required = false) String observaciones,
                            RedirectAttributes ra) {

        workOrderService.updateNotes(workOrderId, diagnostico, observaciones);
        ra.addFlashAttribute("ok", "Orden de trabajo guardada");
        return "redirect:/backoffice/workorders/" + workOrderId;
    }
}
