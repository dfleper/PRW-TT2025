package es.prw.features.workorders.domain;

import java.time.LocalDateTime;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.iam.domain.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "work_orders", uniqueConstraints = {
		@UniqueConstraint(name = "uk_work_orders_appt", columnNames = "id_appointment") })
public class WorkOrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_work_order", nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_appointment", nullable = false, foreignKey = @ForeignKey(name = "fk_work_orders_appt"))
	private AppointmentEntity appointment;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 16)
	private WorkOrderStatus estado = WorkOrderStatus.abierta;

	@Column(name = "opened_at", nullable = false)
	private LocalDateTime openedAt;

	@Column(name = "closed_at")
	private LocalDateTime closedAt;

	// TEXT real en MariaDB (evita que Hibernate espere TINYTEXT/CLOB)
	@Column(name = "diagnostico", columnDefinition = "TEXT")
	private String diagnostico;

	@Column(name = "observaciones", columnDefinition = "TEXT")
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user", foreignKey = @ForeignKey(name = "fk_wo_created_by"))
	private UserEntity createdByUser;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by_user", foreignKey = @ForeignKey(name = "fk_wo_updated_by"))
	private UserEntity updatedByUser;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		if (openedAt == null)
			openedAt = now;
		if (createdAt == null)
			createdAt = now;
		if (updatedAt == null)
			updatedAt = now;
		if (estado == null)
			estado = WorkOrderStatus.abierta;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// ===== getters/setters =====

	public Long getId() {
		return id;
	}

	public AppointmentEntity getAppointment() {
		return appointment;
	}

	public void setAppointment(AppointmentEntity appointment) {
		this.appointment = appointment;
	}

	public WorkOrderStatus getEstado() {
		return estado;
	}

	public void setEstado(WorkOrderStatus estado) {
		this.estado = estado;
	}

	public LocalDateTime getOpenedAt() {
		return openedAt;
	}

	public void setOpenedAt(LocalDateTime openedAt) {
		this.openedAt = openedAt;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public UserEntity getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserEntity createdByUser) {
		this.createdByUser = createdByUser;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public UserEntity getUpdatedByUser() {
		return updatedByUser;
	}

	public void setUpdatedByUser(UserEntity updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
