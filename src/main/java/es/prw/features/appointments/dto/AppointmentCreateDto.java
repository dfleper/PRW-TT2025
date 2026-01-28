package es.prw.features.appointments.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentCreateDto {

    @NotNull(message = "vehicleId es requerido")
    private Long vehicleId;

    @NotNull(message = "serviceId es requerido")
    private Long serviceId;

    @NotNull(message = "startDateTime es requerido")
    private LocalDateTime startDateTime;

    public AppointmentCreateDto() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
