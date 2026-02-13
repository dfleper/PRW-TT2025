package es.prw.features.employees.domain;

public enum EmployeeType {
	RECEPCIONISTA("recepcionista"), MECANICO("mecanico"), JEFE("jefe");

	private final String dbValue;

	EmployeeType(String dbValue) {
		this.dbValue = dbValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static EmployeeType fromDbValue(String value) {
		if (value == null)
			return null;
		for (EmployeeType t : values()) {
			if (t.dbValue.equals(value))
				return t;
		}
		throw new IllegalArgumentException("Unknown tipo: " + value);
	}
}
