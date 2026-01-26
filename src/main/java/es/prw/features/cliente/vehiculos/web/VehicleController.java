package es.prw.features.cliente.vehiculos.web;

import es.prw.features.cliente.vehiculos.dto.VehicleDto;
import es.prw.features.cliente.vehiculos.exception.DuplicateMatriculaException;
import es.prw.features.cliente.vehiculos.exception.DuplicateVinException;
import es.prw.features.cliente.vehiculos.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/vehiculos")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Authentication auth, Model model) {
        model.addAttribute("vehicles", vehicleService.listForClient(auth.getName()));
        return "cliente/vehiculos/list";
    }

    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("vehicle", new VehicleDto());
        model.addAttribute("mode", "create");
        return "cliente/vehiculos/form";
    }

    @PostMapping
    public String create(
            Authentication auth,
            @Valid @ModelAttribute("vehicle") VehicleDto dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (br.hasErrors()) {
            model.addAttribute("mode", "create");
            return "cliente/vehiculos/form";
        }

        try {
            vehicleService.create(auth.getName(), dto);
            ra.addFlashAttribute("ok", "Vehículo creado correctamente");
            return "redirect:/cliente/vehiculos";
        } catch (DuplicateMatriculaException ex) {
            br.rejectValue("matricula", "duplicate", ex.getMessage());
            model.addAttribute("mode", "create");
            return "cliente/vehiculos/form";
        } catch (DuplicateVinException ex) {
            br.rejectValue("vin", "duplicate", ex.getMessage());
            model.addAttribute("mode", "create");
            return "cliente/vehiculos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editForm(Authentication auth, @PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getForEdit(auth.getName(), id));
        model.addAttribute("mode", "edit");
        return "cliente/vehiculos/form";
    }

    @PostMapping("/{id}")
    public String update(
            Authentication auth,
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicle") VehicleDto dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (br.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "cliente/vehiculos/form";
        }

        try {
            vehicleService.update(auth.getName(), id, dto);
            ra.addFlashAttribute("ok", "Vehículo actualizado correctamente");
            return "redirect:/cliente/vehiculos";
        } catch (DuplicateMatriculaException ex) {
            br.rejectValue("matricula", "duplicate", ex.getMessage());
            model.addAttribute("mode", "edit");
            return "cliente/vehiculos/form";
        } catch (DuplicateVinException ex) {
            br.rejectValue("vin", "duplicate", ex.getMessage());
            model.addAttribute("mode", "edit");
            return "cliente/vehiculos/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(Authentication auth, @PathVariable Long id, RedirectAttributes ra) {
        vehicleService.delete(auth.getName(), id);
        ra.addFlashAttribute("ok", "Vehículo eliminado correctamente");
        return "redirect:/cliente/vehiculos";
    }
}
