package es.prw.features.backoffice.agenda.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.employees.domain.EmployeeEntity;

public interface BackofficeAppointmentService {

	List<AppointmentEntity> getAgenda(LocalDate date, Optional<AppointmentStatus> status);

	AppointmentEntity getAppointmentDetail(Long id);

	void changeStatus(Long id, AppointmentStatus newStatus);

	// Para el select de mecánicos
	List<EmployeeEntity> findAllMechanics();

	// Asignar mecánico
	void assignMechanic(Long id, Long mechanicId);
}
