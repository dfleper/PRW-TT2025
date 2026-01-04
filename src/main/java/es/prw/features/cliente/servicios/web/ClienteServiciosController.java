package es.prw.features.cliente.servicios.web;

import java.util.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.service.ServiceCatalogService;
import es.prw.features.cliente.servicios.dto.SelectedServicesForm;

@Controller
@RequestMapping(ClienteServiciosController.BASE)
public class ClienteServiciosController {

    public static final String BASE = "/cliente/servicios";
    private static final String SESSION_SELECTED = "selectedServiceIds";

    private final ServiceCatalogService catalog;

    public ClienteServiciosController(ServiceCatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "nombre") String sort,
            HttpSession session,
            Model model
    ) {
        @SuppressWarnings("unchecked")
        Set<Long> selected = (Set<Long>) session.getAttribute(SESSION_SELECTED);

        if (selected == null) {
            selected = new LinkedHashSet<>();
            session.setAttribute(SESSION_SELECTED, selected);
        }

        List<ServiceEntity> services = catalog.listActive(q, sort);

        SelectedServicesForm form = new SelectedServicesForm();
        form.setServiceIds(new ArrayList<>(selected));

        model.addAttribute("services", services);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedCount", selected.size());
        model.addAttribute("selectionForm", form);

        return "cliente/servicios/list";
    }

    @PostMapping("/seleccion")
    public String saveSelection(
            @ModelAttribute("selectionForm") SelectedServicesForm form,
            HttpSession session,
            RedirectAttributes ra
    ) {
        Set<Long> selected = new LinkedHashSet<>();
        if (form.getServiceIds() != null) {
            selected.addAll(form.getServiceIds());
        }

        session.setAttribute(SESSION_SELECTED, selected);
        ra.addFlashAttribute("msg", "Selección guardada (" + selected.size() + ").");
        return "redirect:" + BASE;
    }

    @PostMapping("/seleccion/limpiar")
    public String clearSelection(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute(SESSION_SELECTED);
        ra.addFlashAttribute("msg", "Selección limpiada.");
        return "redirect:" + BASE;
    }
}
