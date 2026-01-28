package es.prw.features.appointments.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.prw.features.appointments.domain.AppointmentStatus;
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

    // ====== API “de cliente”: elige servicio + hora, calculamos fin ======
    public boolean isAvailable(Long serviceId, LocalDateTime startDateTime) {
        if (serviceId == null || startDateTime == null) return false;

        ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);
        if (service == null || !service.isActivo()) return false;

        long durationMin = 60L;
        Short minutos = service.getMinutosEstimados();
        if (minutos != null && minutos > 0) {
            durationMin = minutos.longValue();
        }

        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMin);

        if (!passesOptionalRules(startDateTime, endDateTime)) return false;

        return !appointmentRepository.existsOverlap(
                startDateTime,
                endDateTime,
                AppointmentStatus.CANCELADA
        );
    }

    // ====== API “técnica”: si ya tienes inicio/fin calculados ======
    public boolean isAvailable(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return false;
        if (!fin.isAfter(inicio)) return false;

        if (!passesOptionalRules(inicio, fin)) return false;

        return !appointmentRepository.existsOverlap(inicio, fin, AppointmentStatus.CANCELADA);
    }

    // ====== NO MVP: disponibilidad por empleado ======
    // Ahora mismo no tienes EmployeeRepository ni módulo de asignación,
    // así que evitamos dependencias y dejamos esto preparado sin romper compilación.
    public boolean isAvailableForEmployee(Long employeeId, LocalDateTime inicio, LocalDateTime fin) {
        // Si todavía no se usa en el MVP, mejor devolver true para no bloquear flujos.
        // Si prefieres "fail closed", cambia a: return false;
        return true;
    }

    private boolean passesOptionalRules(LocalDateTime start, LocalDateTime end) {
        // Regla base: rangos coherentes
        if (start == null || end == null) return false;
        if (!end.isAfter(start)) return false;

        if (!blockWeekends && !enforceHours) return true;

        if (blockWeekends) {
            DayOfWeek d = start.getDayOfWeek();
            if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) return false;
        }

        if (enforceHours) {
            // Evitamos reservas que crucen medianoche
            if (!start.toLocalDate().equals(end.toLocalDate())) return false;

            LocalTime open = safeParseTime(openTimeStr, LocalTime.of(8, 0));
            LocalTime close = safeParseTime(closeTimeStr, LocalTime.of(18, 0));

            LocalTime st = start.toLocalTime();
            LocalTime en = end.toLocalTime();

            return !st.isBefore(open) && !en.isAfter(close);
        }

        return true;
    }

    private LocalTime safeParseTime(String value, LocalTime fallback) {
        try {
            return LocalTime.parse(value);
        } catch (Exception ex) {
            return fallback;
        }
    }
}
