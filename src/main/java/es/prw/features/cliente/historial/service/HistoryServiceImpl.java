package es.prw.features.cliente.historial.service;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.workorders.parts.repository.WorkOrderPartRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistoryServiceImpl implements HistoryService {

    private final VehicleRepository vehicleRepository;
    private final AppointmentRepository appointmentRepository;
    private final WorkOrderPartRepository workOrderPartRepository;

    public HistoryServiceImpl(
            VehicleRepository vehicleRepository,
            AppointmentRepository appointmentRepository,
            WorkOrderPartRepository workOrderPartRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.appointmentRepository = appointmentRepository;
        this.workOrderPartRepository = workOrderPartRepository;
    }

    @Override
    public List<AppointmentEntity> getVehicleHistory(Long vehicleId, Long customerId) {

        // ✅ Propiedad del vehículo
        vehicleRepository.findByIdVehicleAndCustomer_IdCustomer(vehicleId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        return appointmentRepository.findFinalizedHistoryByVehicleId(vehicleId, AppointmentStatus.FINALIZADA);
    }

    @Override
    public HistoryDetail getHistoryDetail(Long appointmentId, Long customerId) {

        AppointmentEntity appt = appointmentRepository
                .findHistoryDetailFinalizedByIdAndCustomer(appointmentId, customerId, AppointmentStatus.FINALIZADA)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intervención no encontrada"));

        if (appt.getWorkOrder() == null) {
            return new HistoryDetail(appt, List.of());
        }

        Long woId = appt.getWorkOrder().getId();

        var parts = workOrderPartRepository.findViewByWorkOrderId(woId);

        return new HistoryDetail(appt, parts);
    }
}
