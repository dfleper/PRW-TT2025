package es.prw.features.appointments.domain;

import java.time.LocalDateTime;

import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.iam.domain.CustomerEntity;
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

/**
 * Entidad JPA para la tabla appointments.
 *
 * En BD:
 * - inicio (fecha/hora inicio)
 * - fin (fecha/hora fin)
 * - estado (para ignorar canceladas en disponibilidad)
 */
@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_CONFIRMADA = "confirmada";
    public static final String ESTADO_EN_CURSO = "en_curso";
    public static final String ESTADO_FINALIZADA = "finalizada";
    public static final String ESTADO_CANCELADA = "cancelada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_appointment", nullable = false)
    private Long idAppointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_customer", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehicle", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_service", nullable = false)
    private ServiceEntity service;

    @Column(name = "id_employee_asignado")
    private Long idEmployeeAsignado;

    @Column(name = "created_by_user")
    private Long createdByUser;

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fin", nullable = false)
    private LocalDateTime fin;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = ESTADO_PENDIENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.estado == null || this.estado.trim().isEmpty()) this.estado = ESTADO_PENDIENTE;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getIdAppointment() { return idAppointment; }
    public void setIdAppointment(Long idAppointment) { this.idAppointment = idAppointment; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public VehicleEntity getVehicle() { return vehicle; }
    public void setVehicle(VehicleEntity vehicle) { this.vehicle = vehicle; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public Long getIdEmployeeAsignado() { return idEmployeeAsignado; }
    public void setIdEmployeeAsignado(Long idEmployeeAsignado) { this.idEmployeeAsignado = idEmployeeAsignado; }

    public Long getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(Long createdByUser) { this.createdByUser = createdByUser; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
