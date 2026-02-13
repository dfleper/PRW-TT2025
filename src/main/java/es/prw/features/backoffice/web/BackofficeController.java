package es.prw.features.backoffice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backoffice")
public class BackofficeController {

	@GetMapping
	public String backoffice() {
		return "backoffice/dashboard";
	}
}
