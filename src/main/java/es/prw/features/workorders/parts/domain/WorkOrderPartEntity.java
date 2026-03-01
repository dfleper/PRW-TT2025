package es.prw.features.workorders.parts.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import es.prw.features.iam.domain.UserEntity;
import es.prw.features.workorders.domain.WorkOrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "work_order_parts")
public class WorkOrderPartEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_wop", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_work_order", nullable = false)
	private WorkOrderEntity workOrder;

	@Column(name = "id_part", nullable = false)
	private Long partId;

	@Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
	private BigDecimal quantity;

	@Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Column(name = "notes", length = 255)
	private String notes;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user")
	private UserEntity createdByUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by_user")
	private UserEntity updatedByUser;

	@PrePersist
	void onCreate() {
		if (this.createdAt == null) this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public WorkOrderEntity getWorkOrder() { return workOrder; }
	public void setWorkOrder(WorkOrderEntity workOrder) { this.workOrder = workOrder; }
	public Long getPartId() { return partId; }
	public void setPartId(Long partId) { this.partId = partId; }
	public BigDecimal getQuantity() { return quantity; }
	public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
	public BigDecimal getTotal() { return total; }
	public void setTotal(BigDecimal total) { this.total = total; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public UserEntity getCreatedByUser() { return createdByUser; }
	public void setCreatedByUser(UserEntity createdByUser) { this.createdByUser = createdByUser; }
	public UserEntity getUpdatedByUser() { return updatedByUser; }
	public void setUpdatedByUser(UserEntity updatedByUser) { this.updatedByUser = updatedByUser; }
}