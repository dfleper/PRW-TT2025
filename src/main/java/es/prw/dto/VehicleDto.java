package es.prw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VehicleDto {

    private Long id;

    @NotBlank(message = "La matrícula es obligatoria")
    @Size(max = 12, message = "Máximo 12 caracteres")
    @Pattern(regexp = "^[0-9]{4}[\\s-]?[A-Za-z]{3}$", message = "Formato esperado: 1234ABC")
    private String matricula;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String modelo;

    @Min(value = 1900, message = "Año inválido")
    @Max(value = 2100, message = "Año inválido")
    private Integer anio;

    @Size(max = 20, message = "Máximo 20 caracteres")
    private String combustible;

    @Size(max = 2000, message = "Máximo 2000 caracteres")
    private String notas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
