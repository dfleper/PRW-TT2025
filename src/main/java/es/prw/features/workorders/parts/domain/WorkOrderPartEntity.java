package es.prw.features.workorders.parts.domain;

import es.prw.features.workorders.domain.WorkOrderEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_parts")
public class WorkOrderPartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_wop", nullable = false)
    private Long id;

    // ========= Relación =========
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_work_order", nullable = false)
    private WorkOrderEntity workOrder;

    // ========= FK directa (sin relación JPA a PartEntity) =========
    @Column(name = "id_part", nullable = false)
    private Long partId;

    // ========= Datos =========
    // ⚠️ Para 3.5 debes tener en BD: cantidad DECIMAL(10,2)
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "notes", length = 255)
    private String notes;

    // ========= Auditoría (en BD ya hay defaults; esto es OK igual) =========
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========= Getters / Setters =========
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
}
