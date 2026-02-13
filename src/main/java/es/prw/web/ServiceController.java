package es.prw.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.prw.features.catalog.service.ServiceCatalogService;

@Controller
public class ServiceController {

	private final ServiceCatalogService catalog;

	public ServiceController(ServiceCatalogService catalog) {
		this.catalog = catalog;
	}

	@GetMapping("/servicios")
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false, defaultValue = "nombre") String sort, Model model) {
		model.addAttribute("services", catalog.listActive(q, sort));
		return "servicios/list";
	}
}
