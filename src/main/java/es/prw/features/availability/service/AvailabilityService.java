package es.prw.features.availability.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;

@Service
public class AvailabilityService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    // Opcional (MVP): reglas adicionales
    @Value("${tt.workshop.enforce-hours:false}")
    private boolean enforceHours;

    @Value("${tt.workshop.open-time:08:00}")
    private String openTimeStr;

    @Value("${tt.workshop.close-time:18:00}")
    private String closeTimeStr;

    @Value("${tt.workshop.block-weekends:false}")
    private boolean blockWeekends;

    public AvailabilityService(AppointmentRepository appointmentRepository, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Comprueba si hay disponibilidad para un servicio en una fecha/hora de inicio.
     * Calcula el fin sumando los minutos estimados del servicio.
     */
    public boolean isAvailable(Long serviceId, LocalDateTime startDateTime) {
        if (serviceId == null || startDateTime == null) return false;

        ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);
        if (service == null || !service.isActivo()) return false;

        long durationMin = 60;
        if (service.getMinutosEstimados() != null) {
            durationMin = service.getMinutosEstimados().longValue();
            if (durationMin <= 0) durationMin = 60;
        }

        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMin);

        if (!passesOptionalRules(startDateTime, endDateTime)) return false;

        List<String> ignored = List.of(AppointmentEntity.ESTADO_CANCELADA);
        // Si en tu negocio quieres que 'finalizada' NO bloquee, añade:
        // List<String> ignored = List.of(AppointmentEntity.ESTADO_CANCELADA, AppointmentEntity.ESTADO_FINALIZADA);

        return appointmentRepository.findOverlaps(startDateTime, endDateTime, ignored).isEmpty();
    }

    private boolean passesOptionalRules(LocalDateTime start, LocalDateTime end) {
        if (!blockWeekends && !enforceHours) return true;

        if (blockWeekends) {
            DayOfWeek d = start.getDayOfWeek();
            if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) return false;
        }

        if (enforceHours) {
            // Evitamos reservas que crucen medianoche
            if (!start.toLocalDate().equals(end.toLocalDate())) return false;

            LocalTime open = LocalTime.parse(openTimeStr);
            LocalTime close = LocalTime.parse(closeTimeStr);

            LocalTime st = start.toLocalTime();
            LocalTime en = end.toLocalTime();

            return !st.isBefore(open) && !en.isAfter(close);
        }

        return true;
    }
}
