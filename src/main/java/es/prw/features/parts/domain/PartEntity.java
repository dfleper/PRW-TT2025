package es.prw.features.parts.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parts")
public class PartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_part", nullable = false)
    private Long id;

    @Column(name = "sku", nullable = false, length = 40)
    private String sku;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;

    /**
     * ⚠️ Este campo SOLO funciona si has creado la columna en BD:
     * ALTER TABLE parts ADD allows_decimal TINYINT(1) NOT NULL DEFAULT 0;
     */
    @Column(name = "allows_decimal", nullable = false)
    private Boolean allowsDecimal = false;

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecioUnit() { return precioUnit; }
    public Boolean getAllowsDecimal() { return allowsDecimal; }
}
