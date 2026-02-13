package es.prw.features.employees.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EmployeeTypeConverter implements AttributeConverter<EmployeeType, String> {

	@Override
	public String convertToDatabaseColumn(EmployeeType attribute) {
		return attribute != null ? attribute.getDbValue() : null;
	}

	@Override
	public EmployeeType convertToEntityAttribute(String dbData) {
		return EmployeeType.fromDbValue(dbData);
	}
}
