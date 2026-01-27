package es.prw.features.appointments.domain;

import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.employees.domain.EmployeeEntity;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_appointment", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_customer", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehicle", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_service", nullable = false)
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_asignado")
    private EmployeeEntity employeeAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user")
    private UserEntity updatedByUser;

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fin", nullable = false)
    private LocalDateTime fin;

    @Convert(converter = AppointmentStatusConverter.class)
    @Column(name = "estado", nullable = false, columnDefinition = "enum('pendiente','confirmada','en_curso','finalizada','cancelada')")
    private AppointmentStatus estado = AppointmentStatus.PENDIENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ===== lifecycle para respetar NOT NULL sin pelearte con defaults =====
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (estado == null) estado = AppointmentStatus.PENDIENTE;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== getters/setters (mínimos) =====
    public Long getId() { return id; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public VehicleEntity getVehicle() { return vehicle; }
    public void setVehicle(VehicleEntity vehicle) { this.vehicle = vehicle; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public EmployeeEntity getEmployeeAsignado() { return employeeAsignado; }
    public void setEmployeeAsignado(EmployeeEntity employeeAsignado) { this.employeeAsignado = employeeAsignado; }

    public UserEntity getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(UserEntity createdByUser) { this.createdByUser = createdByUser; }

    public UserEntity getUpdatedByUser() { return updatedByUser; }
    public void setUpdatedByUser(UserEntity updatedByUser) { this.updatedByUser = updatedByUser; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public AppointmentStatus getEstado() { return estado; }
    public void setEstado(AppointmentStatus estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
