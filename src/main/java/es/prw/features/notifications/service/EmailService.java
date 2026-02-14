package es.prw.features.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true", matchIfMissing = false)
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:no-reply@turbotaller.local}")
  private String mailFrom;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendAppointmentConfirmation(String to, AppointmentMailData data) {
    try {
      SimpleMailMessage msg = new SimpleMailMessage();
      msg.setFrom(mailFrom);
      msg.setTo(to);
      msg.setSubject("Confirmación de cita — Turbo Taller");
      msg.setText(buildPlainTextBody(data));

      mailSender.send(msg);

      log.info("[MAIL] Appointment confirmation sent to={} appointmentId={}", to, safeId(data));

    } catch (MailException ex) {
      log.warn("[MAIL] Failed to send appointment confirmation to={} appointmentId={} cause={}",
          to, safeId(data), ex.getMessage());

    } catch (Exception ex) {
      log.warn("[MAIL] Unexpected error sending appointment confirmation to={} appointmentId={} cause={}",
          to, safeId(data), ex.getMessage());
    }
  }

  private String buildPlainTextBody(AppointmentMailData d) {
    return """
        Hola %s,

        Tu cita ha sido registrada en Turbo Taller.

        Servicio: %s
        Vehículo: %s
        Fecha/hora: %s
        Estado: %s

        Gracias,
        Turbo Taller
        """.formatted(nullToDash(d.customerName()),
        nullToDash(d.serviceName()),
        nullToDash(d.vehiclePlate()),
        nullToDash(d.dateTimeText()),
        nullToDash(d.status()));
  }

  private String nullToDash(String s) {
    return (s == null || s.isBlank()) ? "-" : s;
  }

  private Long safeId(AppointmentMailData data) {
    return (data == null) ? null : data.appointmentId();
  }
}
