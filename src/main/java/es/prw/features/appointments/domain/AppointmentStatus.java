package es.prw.features.appointments.domain;

/**
 * Debe coincidir EXACTAMENTE con los valores del ENUM en MariaDB:
 * ('pendiente','confirmada','en_curso','finalizada','cancelada')
 */
public enum AppointmentStatus {
    pendiente,
    confirmada,
    en_curso,
    finalizada,
    cancelada
}
