package es.prw.features.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.prw.features.auth.dto.RegisterRequest;
import es.prw.features.auth.service.RegisterService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	private final RegisterService registerService;

	public AuthController(RegisterService registerService) {
		this.registerService = registerService;
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		return "auth/register";
	}

	@PostMapping("/register")
	public String doRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest form,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "auth/register";
		}

		try {
			registerService.registerCliente(form);
		} catch (IllegalArgumentException ex) {
			if ("EMAIL_EXISTS".equals(ex.getMessage())) {
				bindingResult.rejectValue("email", "email.exists", "El email ya está registrado");
				return "auth/register";
			}
			throw ex;
		}

		return "redirect:/login?registered";
	}
}
