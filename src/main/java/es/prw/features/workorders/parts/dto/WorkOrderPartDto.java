package es.prw.features.workorders.parts.dto;

import java.math.BigDecimal;

public class WorkOrderPartDto {

	private Long partId;

	// Ahora permite 3.5
	private BigDecimal quantity;

	private String notes;

	public Long getPartId() {
		return partId;
	}

	public void setPartId(Long partId) {
		this.partId = partId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
