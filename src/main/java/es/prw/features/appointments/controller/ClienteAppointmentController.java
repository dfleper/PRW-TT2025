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

    // ========= CHECK 6 =========
    @GetMapping("/nueva")
    public String nuevaCitaForm(Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        List<VehicleEntity> vehicles =
                vehicleRepository.findByCustomer_IdCustomerAndActivoTrueOrderByMarcaAscModeloAscMatriculaAsc(customerId);

        List<ServiceEntity> services =
                serviceRepository.findByActivoTrueOrderByNombreAsc();

        model.addAttribute("dto", new AppointmentCreateDto());
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", services);

        return "cliente/citas/form";
    }

    // ========= CHECK 7 =========
    @PostMapping
    public String crearCita(
            @Valid @ModelAttribute("dto") AppointmentCreateDto dto,
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
            appointmentId = appointmentService.createAppointment(dto);
        } catch (ResponseStatusException ex) {

            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                model.addAttribute("errorDisponibilidad", ex.getReason());
                reloadFormLists(model);
                return "cliente/citas/form";
            }

            throw ex;
        }

        redirectAttributes.addFlashAttribute("success", "Cita creada correctamente");
        return "redirect:/cliente/citas/" + appointmentId;
    }

    // ========= CHECK 8 =========
    @GetMapping("/{id}")
    public String detalleCita(@PathVariable Long id, Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        AppointmentEntity appointment = appointmentRepository
                .findDetailByIdAndCustomer(id, customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));

        model.addAttribute("appointment", appointment);

        return "cliente/citas/detalle";
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
