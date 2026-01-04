package es.prw.features.cliente.vehiculos.domain;

import es.prw.features.iam.domain.CustomerEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = @UniqueConstraint(name = "uk_vehicles_matricula", columnNames = "matricula")
)
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle", nullable = false)
    private Long idVehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_customer", nullable = false)
    private CustomerEntity customer;

    @Column(name = "matricula", nullable = false, length = 12)
    private String matricula;

    @Column(name = "marca", length = 60)
    private String marca;

    @Column(name = "modelo", length = 60)
    private String modelo;
    
    @Column(name = "anio")
    private Short anio;

    @Column(name = "combustible", length = 20)
    private String combustible;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Long getIdVehicle() { return idVehicle; }
    public void setIdVehicle(Long idVehicle) { this.idVehicle = idVehicle; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Short getAnio() { return anio; }
    public void setAnio(Short anio) { this.anio = anio; }

    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
