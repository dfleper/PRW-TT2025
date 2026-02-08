package es.prw.features.appointments.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.dto.AppointmentCreateDto;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.appointments.service.AppointmentService;
import es.prw.features.catalog.repository.ServiceRepository;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // ========= T12: listado =========
    @GetMapping
    public String listado(Model model) {
        Long customerId = getCurrentCustomerIdOrThrow();

        List<AppointmentEntity> appointments =
                appointmentRepository.findListByCustomerWithJoinsDesc(customerId);

        // Calculamos qué citas se pueden cancelar para NO usar T(...) en Thymeleaf
        Set<Long> cancelableIds = new HashSet<>();
        for (AppointmentEntity a : appointments) {
            boolean canCancel =
                    a.getEstado() != AppointmentStatus.CANCELADA
                    && a.getEstado() != AppointmentStatus.FINALIZADA
                    && a.getEstado() != AppointmentStatus.EN_CURSO;
            if (canCancel) cancelableIds.add(a.getId());
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("cancelableIds", cancelableIds);

        return "cliente/citas/list";
    }

    // ========= T11: form nueva cita =========
    @GetMapping("/nueva")
    public String nuevaCitaForm(Model model) {
        reloadFormLists(model);
        return "cliente/citas/form";
    }

    // ========= T11: crear cita =========
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

        try {
            Long appointmentId = appointmentService.createAppointment(appointment);
            redirectAttributes.addFlashAttribute("success", "Cita creada correctamente");
            return "redirect:/cliente/citas/" + appointmentId;

        } catch (ResponseStatusException ex) {

            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                model.addAttribute(
                        "availabilityError",
                        ex.getReason() != null ? ex.getReason() : "No hay disponibilidad para esa franja horaria."
                );
                reloadFormLists(model);
                return "cliente/citas/form";
            }

            throw ex;
        }
    }

    // ========= T12: detalle =========
    @GetMapping("/{id}")
    public String detalleCita(@PathVariable Long id, Model model) {

        Long customerId = getCurrentCustomerIdOrThrow();

        AppointmentEntity appointment = appointmentRepository
                .findDetailByIdAndCustomer(id, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        boolean canCancel =
                appointment.getEstado() != AppointmentStatus.CANCELADA
                && appointment.getEstado() != AppointmentStatus.FINALIZADA
                && appointment.getEstado() != AppointmentStatus.EN_CURSO;

        model.addAttribute("appointment", appointment);
        model.addAttribute("canCancel", canCancel);

        return "cliente/citas/detail";
    }

    // ========= T12: cancelar =========
    @PostMapping("/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        Long customerId = getCurrentCustomerIdOrThrow();

        AppointmentEntity appointment = appointmentRepository
                .findDetailByIdAndCustomer(id, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        if (appointment.getEstado() == AppointmentStatus.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita finalizada");
        }
        if (appointment.getEstado() == AppointmentStatus.EN_CURSO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una cita en curso");
        }
        if (appointment.getEstado() == AppointmentStatus.CANCELADA) {
            redirectAttributes.addFlashAttribute("info", "La cita ya estaba cancelada");
            return "redirect:/cliente/citas/" + id;
        }

        // (Opcional MVP) No permitir cancelar si faltan < 24h
        LocalDateTime inicio = appointment.getInicio();
        if (inicio != null) {
            Duration d = Duration.between(LocalDateTime.now(), inicio);
            if (!d.isNegative() && d.toHours() < 24) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No se puede cancelar una cita con menos de 24h de antelación"
                );
            }
        }

        appointment.setEstado(AppointmentStatus.CANCELADA);
        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("success", "Cita cancelada correctamente");
        return "redirect:/cliente/citas/" + id;
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

        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new AppointmentCreateDto());
        }
    }

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
