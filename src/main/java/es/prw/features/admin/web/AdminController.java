package es.prw.features.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@GetMapping("/admin")
	public String index() {
		return "admin/index";
	}
}
