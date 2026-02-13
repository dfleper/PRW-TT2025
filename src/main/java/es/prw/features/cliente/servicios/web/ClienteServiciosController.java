package es.prw.features.cliente.servicios.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.service.ServiceCatalogService;
import es.prw.features.cliente.servicios.dto.SelectedServiceForm;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(ClienteServiciosController.BASE)
public class ClienteServiciosController {

	public static final String BASE = "/cliente/servicios";
	private static final String SESSION_SELECTED = "selectedServiceId";

	private final ServiceCatalogService catalog;

	public ClienteServiciosController(ServiceCatalogService catalog) {
		this.catalog = catalog;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false, defaultValue = "nombre") String sort, HttpSession session, Model model) {
		Long selectedId = (Long) session.getAttribute(SESSION_SELECTED);

		List<ServiceEntity> services = catalog.listActive(q, sort);

		SelectedServiceForm form = new SelectedServiceForm();
		form.setServiceId(selectedId); // Si ya había selección en sesión, marcar

		model.addAttribute("services", services);
		model.addAttribute("q", q == null ? "" : q);
		model.addAttribute("sort", sort);
		model.addAttribute("selectedId", selectedId);
		model.addAttribute("selectionForm", form);

		return "cliente/servicios/list";
	}

	/**
	 * Selecciona un servicio y redirige a /cliente/citas/nueva. (UX pro: no
	 * guardamos explícitamente; la selección sirve para iniciar la reserva).
	 */
	@PostMapping("/seleccion-y-reservar")
	public String selectAndReserve(@ModelAttribute("selectionForm") SelectedServiceForm form, HttpSession session,
			RedirectAttributes ra) {
		if (form.getServiceId() == null) {
			ra.addFlashAttribute("error", "Debes seleccionar un servicio.");
			return "redirect:" + BASE;
		}

		session.setAttribute(SESSION_SELECTED, form.getServiceId());
		return "redirect:/cliente/citas/nueva";
	}
}
