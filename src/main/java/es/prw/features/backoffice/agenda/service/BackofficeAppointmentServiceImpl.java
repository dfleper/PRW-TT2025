package es.prw.features.backoffice.agenda.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.employees.domain.EmployeeEntity;
import es.prw.features.employees.domain.EmployeeType;
import es.prw.features.employees.repository.EmployeeRepository;
import es.prw.features.workorders.domain.WorkOrderStatus;
import es.prw.features.workorders.repository.WorkOrderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BackofficeAppointmentServiceImpl implements BackofficeAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkOrderRepository workOrderRepository;

    public BackofficeAppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                           EmployeeRepository employeeRepository,
                                           WorkOrderRepository workOrderRepository) {
        this.appointmentRepository = appointmentRepository;
        this.employeeRepository = employeeRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEntity> getAgenda(LocalDate date, Optional<AppointmentStatus> status) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay(); // [start, end)

        if (status != null && status.isPresent()) {
            return appointmentRepository.findAgendaByDayAndStatus(start, end, status.get());
        }
        return appointmentRepository.findAgendaByDay(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentEntity getAppointmentDetail(Long id) {
        return appointmentRepository.findDetailByIdWithJoins(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    @Override
    @Transactional
    public void changeStatus(Long id, AppointmentStatus newStatus) {
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido");
        }

        AppointmentEntity appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        AppointmentStatus current = appt.getEstado();

        if (current == AppointmentStatus.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cambiar una cita cancelada");
        }

        // Regla actual: finalizar solo si está EN_CURSO
        if (newStatus == AppointmentStatus.FINALIZADA && current != AppointmentStatus.EN_CURSO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede finalizar si está EN_CURSO");
        }

        // ✅ T17: NO permitir finalizar si no hay OT cerrada
        if (newStatus == AppointmentStatus.FINALIZADA) {

            var wo = workOrderRepository.findByAppointment_Id(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "No existe orden de trabajo asociada: no se puede finalizar la cita"
                    ));

            if (wo.getEstado() != WorkOrderStatus.cerrada) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La orden de trabajo debe estar CERRADA para finalizar la cita"
                );
            }
        }

        appt.setEstado(newStatus);
        appointmentRepository.save(appt);
    }

    // ✅ devuelve mecánicos activos con user cargado (para Thymeleaf)
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeEntity> findAllMechanics() {
        return employeeRepository.findMecanicosActivosWithUser();
    }

    @Override
    @Transactional
    public void assignMechanic(Long id, Long mechanicId) {
        if (mechanicId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mecánico inválido");
        }

        AppointmentEntity appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        if (appt.getEstado() == AppointmentStatus.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede asignar mecánico a una cita cancelada");
        }

        EmployeeEntity emp = employeeRepository.findById(mechanicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        if (Boolean.FALSE.equals(emp.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El empleado está inactivo");
        }

        if (emp.getTipo() != EmployeeType.MECANICO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El empleado no es mecánico");
        }

        appt.setEmployeeAsignado(emp);
        appointmentRepository.save(appt);
    }
}
