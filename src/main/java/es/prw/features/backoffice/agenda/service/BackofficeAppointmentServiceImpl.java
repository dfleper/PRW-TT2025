package es.prw.features.backoffice.agenda.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BackofficeAppointmentServiceImpl implements BackofficeAppointmentService {

    private final AppointmentRepository appointmentRepository;

    public BackofficeAppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentEntity> getAgenda(LocalDate date, Optional<AppointmentStatus> status) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay(); // [start, end)

        if (status != null && status.isPresent()) {
            return appointmentRepository.findAgendaByDayAndStatus(start, end, status.get());
        }

        return appointmentRepository.findAgendaByDay(start, end);
    }
}
