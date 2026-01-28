package es.prw.features.appointments.service;

import es.prw.features.appointments.dto.AppointmentCreateDto;

public interface AppointmentService {
    Long createAppointment(AppointmentCreateDto dto);
}
