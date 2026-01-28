package es.prw.features.appointments.controller;

import java.util.List;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.dto.AppointmentCreateDto;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.appointments.service.AppointmentService;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/citas")
public class ClienteAppointmentController {

    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    public ClienteAppointmentController(
            VehicleRepository vehicleRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AppointmentService appointmentService,
            AppointmentRepository appointmentRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }

    // ========= CHECK: form nueva cita =========
    @GetMapping("/nueva")
    public String nuevaCitaForm(Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        List<VehicleEntity> vehicles =
                vehicleRepository.findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(customerId);

        List<ServiceEntity> services =
                serviceRepository.findByActivoTrueOrderByNombreAsc();

        // 🔥 IMPORTANTE: el nombre debe coincidir con th:object en el template
        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new AppointmentCreateDto());
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", services);

        return "cliente/citas/form";
    }

    // ========= CHECK: crear cita =========
    @PostMapping
    public String crearCita(
            @Valid @ModelAttribute("appointment") AppointmentCreateDto appointment,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            reloadFormLists(model);
            return "cliente/citas/form";
        }

        final Long appointmentId;
        try {
            // Tu servicio debe validar:
            // - vehículo pertenece al cliente logueado (403/404)
            // - disponibilidad (409 CONFLICT) -> NO guardar
            appointmentId = appointmentService.createAppointment(appointment);

        } catch (ResponseStatusException ex) {

            // NO disponibilidad -> mensaje claro y NO guardar
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                model.addAttribute(
                        "availabilityError",
                        ex.getReason() != null ? ex.getReason() : "No hay disponibilidad para esa franja."
                );
                reloadFormLists(model);
                return "cliente/citas/form";
            }

            // Vehículo de otro cliente -> 403 o 404 (según tu service)
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN || ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ex;
            }

            throw ex;
        }

        redirectAttributes.addFlashAttribute("success", "Cita creada correctamente");
        return "redirect:/cliente/citas/" + appointmentId;
    }

    // ========= CHECK: detalle/confirmación =========
    @GetMapping("/{id}")
    public String detalleCita(@PathVariable Long id, Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        AppointmentEntity appointment = appointmentRepository
                .findDetailByIdAndCustomer(id, customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));

        // Para que tu detail.html pueda mostrar servicio/vehículo:
        // O bien el appointment viene con relaciones cargadas (join fetch),
        // o en el template accedes a appointment.vehicle / appointment.service.
        model.addAttribute("appointment", appointment);

        return "cliente/citas/detail"; // ✅ según tu checklist (detail.html)
    }

    // ========= helpers =========
    private void reloadFormLists(Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        model.addAttribute(
                "vehicles",
                vehicleRepository.findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(customerId)
        );

        model.addAttribute(
                "services",
                serviceRepository.findByActivoTrueOrderByNombreAsc()
        );

        // 🔥 si por cualquier motivo no viene el appointment al re-render, lo aseguramos
        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new AppointmentCreateDto());
        }
    }

    private Long getCurrentCustomerIdOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = auth.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        CustomerEntity customer = customerRepository.findByUser_IdUser(user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El usuario no es cliente"));

        return customer.getIdCustomer();
    }
}
