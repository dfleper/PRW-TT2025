package es.prw.features.notifications.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import es.prw.features.notifications.service.AppointmentMailData;
import es.prw.features.notifications.service.EmailService;

@RestController
@Profile("dev")
public class DevMailTestController {

	private final EmailService emailService;

	public DevMailTestController(EmailService emailService) {
		this.emailService = emailService;
	}

	@GetMapping("/dev/test-mail")
	public String testMail() {
		String dt = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

		var data = new AppointmentMailData(999L, "Domingo", "Cambio de aceite", "1234-ABC", dt, "PENDING");

		emailService.sendAppointmentConfirmation("cliente@demo.local", data);

		return "OK (mail sent best-effort). Check MailHog inbox.";
	}
}
