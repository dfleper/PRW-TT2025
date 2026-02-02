package es.prw.features.backoffice.agenda.service;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.domain.AppointmentEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BackofficeAppointmentService {
    List<AppointmentEntity> getAgenda(LocalDate date, Optional<AppointmentStatus> status);
}
