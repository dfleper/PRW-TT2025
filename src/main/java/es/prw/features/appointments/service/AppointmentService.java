package es.prw.features.appointments.service;

import java.util.List;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.dto.AppointmentCreateDto;

public interface AppointmentService {

	Long createAppointment(AppointmentCreateDto dto);

	// Mis citas
	List<AppointmentEntity> listMyAppointments();

	AppointmentEntity getMyAppointment(Long appointmentId);

	void cancelMyAppointment(Long appointmentId);
}
