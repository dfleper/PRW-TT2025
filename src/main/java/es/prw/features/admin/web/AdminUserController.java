package es.prw.features.admin.web;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.prw.features.admin.service.AdminUserService;
import es.prw.features.iam.domain.RoleEntity;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping
	public String list(Model model) {

		List<RoleEntity> allRoles = adminUserService.listAllRoles();
		allRoles.sort(Comparator.comparing(r -> r.getNombre() != null ? r.getNombre() : ""));

		model.addAttribute("users", adminUserService.listUsersWithRoles());
		model.addAttribute("allRoles", allRoles);
		return "admin/users";
	}

	@PostMapping("/{id}/roles")
	public String updateRoles(@PathVariable("id") Long userId,
			@RequestParam(name = "roles", required = false) Set<String> roles, Authentication auth,
			RedirectAttributes ra) {
		try {
			adminUserService.updateUserRoles(userId, roles, auth);
			ra.addFlashAttribute("ok", "Roles actualizados.");
		} catch (ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		} catch (Exception ex) {
			ra.addFlashAttribute("error", "No se pudieron actualizar los roles.");
		}
		return "redirect:/admin/users";
	}

	@PostMapping("/{id}/toggle")
	public String toggleActive(@PathVariable("id") Long userId, Authentication auth, RedirectAttributes ra) {
		try {
			adminUserService.toggleActive(userId, auth);
			ra.addFlashAttribute("ok", "Estado actualizado.");
		} catch (ResponseStatusException ex) {
			ra.addFlashAttribute("error", ex.getReason());
		} catch (Exception ex) {
			ra.addFlashAttribute("error", "No se pudo actualizar el estado.");
		}
		return "redirect:/admin/users";
	}
}
