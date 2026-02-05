package es.prw.features.backoffice.agenda.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.employees.domain.EmployeeEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BackofficeAppointmentService {

    List<AppointmentEntity> getAgenda(LocalDate date, Optional<AppointmentStatus> status);

    AppointmentEntity getAppointmentDetail(Long id);

    void changeStatus(Long id, AppointmentStatus newStatus);

    // ✅ para el select de mecánicos
    List<EmployeeEntity> findAllMechanics();

    // ✅ asignar mecánico
    void assignMechanic(Long id, Long mechanicId);
}
