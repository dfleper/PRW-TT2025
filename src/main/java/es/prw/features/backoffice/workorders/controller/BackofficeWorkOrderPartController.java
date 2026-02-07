package es.prw.features.backoffice.workorders.controller;

import es.prw.features.workorders.parts.dto.WorkOrderPartDto;
import es.prw.features.workorders.parts.service.WorkOrderPartService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/backoffice/workorders")
public class BackofficeWorkOrderPartController {

    private final WorkOrderPartService partService;

    public BackofficeWorkOrderPartController(WorkOrderPartService partService) {
        this.partService = partService;
    }

    @PostMapping("/{id}/parts")
    public String addPart(
            @PathVariable("id") Long workOrderId,
            @ModelAttribute("part") WorkOrderPartDto dto,
            RedirectAttributes ra
    ) {
        try {
            partService.addPart(workOrderId, dto);
            ra.addFlashAttribute("ok", "Pieza añadida");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/backoffice/workorders/" + workOrderId;
    }

    @PostMapping("/{id}/parts/{partId}")
    public String updatePart(
            @PathVariable("id") Long workOrderId,
            @PathVariable("partId") Long partId,
            @ModelAttribute("part") WorkOrderPartDto dto,
            RedirectAttributes ra
    ) {
        try {
            partService.updatePart(partId, dto);
            ra.addFlashAttribute("ok", "Pieza actualizada");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/backoffice/workorders/" + workOrderId;
    }

    @PostMapping("/{id}/parts/{partId}/delete")
    public String deletePart(
            @PathVariable("id") Long workOrderId,
            @PathVariable("partId") Long partId,
            RedirectAttributes ra
    ) {
        try {
            partService.deletePart(partId);
            ra.addFlashAttribute("ok", "Pieza eliminada");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/backoffice/workorders/" + workOrderId;
    }
}
