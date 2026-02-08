package es.prw.features.cliente.historial.web;

import es.prw.features.cliente.historial.service.HistoryService;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteHistoryController {

    private final HistoryService historyService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public ClienteHistoryController(
            HistoryService historyService,
            UserRepository userRepository,
            CustomerRepository customerRepository
    ) {
        this.historyService = historyService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    // GET /cliente/vehiculos/{id}/historial -> listado
    @GetMapping("/cliente/vehiculos/{vehicleId}/historial")
    public String historialPorVehiculo(@PathVariable Long vehicleId, Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        var items = historyService.getVehicleHistory(vehicleId, customerId);

        model.addAttribute("vehicleId", vehicleId);
        model.addAttribute("items", items);

        return "cliente/historial/list";
    }

    // GET /cliente/historial/citas/{appointmentId} -> detalle
    @GetMapping("/cliente/historial/citas/{appointmentId}")
    public String detalleHistorial(@PathVariable Long appointmentId, Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        var detail = historyService.getHistoryDetail(appointmentId, customerId);

        model.addAttribute("appointment", detail.appointment());
        model.addAttribute("parts", detail.parts());

        return "cliente/historial/detail";
    }

    // ========= helper =========
    private Long getCurrentCustomerIdOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = auth.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        CustomerEntity customer = customerRepository.findByUser_IdUser(user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no es cliente"));

        return customer.getIdCustomer();
    }
}
