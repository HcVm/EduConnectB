package com.EduConnectB.app.models;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;
    private String correoElectronico;
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    @OneToMany(mappedBy = "usuario")
    private List<Asesor> asesores;

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public EstadoUsuario getEstado() {
		return estado;
	}

	public void setEstado(EstadoUsuario estado) {
		this.estado = estado;
	}

	public List<Asesor> getAsesores() {
		return asesores;
	}

	public void setAsesores(List<Asesor> asesores) {
		this.asesores = asesores;
	}

	public Usuario(String nombre, String correoElectronico, String contrasena, TipoUsuario tipoUsuario,
			LocalDateTime fechaRegistro, EstadoUsuario estado, List<Asesor> asesores) {
		this.nombre = nombre;
		this.correoElectronico = correoElectronico;
		this.contrasena = contrasena;
		this.tipoUsuario = tipoUsuario;
		this.fechaRegistro = fechaRegistro;
		this.estado = estado;
		this.asesores = asesores;
	}

	public Usuario() {

	}

	
}
