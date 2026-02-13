package es.prw.features.iam.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user", nullable = false)
	private Long idUser;

	@Column(name = "email", nullable = false, unique = true, length = 120)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "nombre", nullable = false, length = 80)
	private String nombre;

	@Column(name = "apellidos", nullable = false, length = 120)
	private String apellidos;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Column(name = "activo", nullable = false)
	private Boolean activo = true;

	// LOPDGDD
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	// Auditoría (self-FK -> users.id_user) - NULL permitido
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "created_by_user", referencedColumnName = "id_user")
	private UserEntity createdByUser;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "updated_by_user", referencedColumnName = "id_user")
	private UserEntity updatedByUser;

	// Gestionados por la BD (DEFAULT CURRENT_TIMESTAMP / ON UPDATE
	// CURRENT_TIMESTAMP)
	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role"))
	private Set<RoleEntity> roles = new HashSet<>();

	// ===== Getters / Setters =====

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public UserEntity getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserEntity createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserEntity getUpdatedByUser() {
		return updatedByUser;
	}

	public void setUpdatedByUser(UserEntity updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	// Solo lectura: los rellena la BD
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Set<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}
}
