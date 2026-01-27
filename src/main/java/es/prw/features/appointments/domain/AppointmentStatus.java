package es.prw.features.appointments.domain;

public enum AppointmentStatus {
    PENDIENTE("pendiente"),
    CONFIRMADA("confirmada"),
    EN_CURSO("en_curso"),
    FINALIZADA("finalizada"),
    CANCELADA("cancelada");

    private final String dbValue;

    AppointmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static AppointmentStatus fromDbValue(String value) {
        if (value == null) return null;
        for (AppointmentStatus s : values()) {
            if (s.dbValue.equals(value)) return s;
        }
        throw new IllegalArgumentException("Unknown estado value: " + value);
    }
}
