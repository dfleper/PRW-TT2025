package es.prw.features.notifications.service;

public record AppointmentMailData(
        Long appointmentId,
        String customerName,
        String serviceName,
        String vehiclePlate,
        String dateTimeText,
        String status
) {}
