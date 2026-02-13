package es.prw.features.iam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_role", nullable = false)
	private Short idRole;

	@Column(name = "nombre", nullable = false, length = 30, unique = true)
	private String nombre;

	public Short getIdRole() {
		return idRole;
	}

	public void setIdRole(Short idRole) {
		this.idRole = idRole;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
