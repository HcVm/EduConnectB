package com.EduConnectB.app.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sesion")
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSesion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_asesor")
    private Asesor asesor;

    private LocalDateTime fechaHora;
    
    @Enumerated(EnumType.STRING)
    private EstadoSesion estado;
    
    @OneToOne(mappedBy = "sesion")
    @JsonIgnore
    private Valoracion valoracion;
    
    private String urlJitsi;
    
    @Transient
    private List<ObjectError> errores;

	public Integer getIdSesion() {
		return idSesion;
	}

	public void setIdSesion(Integer idSesion) {
		this.idSesion = idSesion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Asesor getAsesor() {
		return asesor;
	}

	public void setAsesor(Asesor asesor) {
		this.asesor = asesor;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public EstadoSesion getEstado() {
		return estado;
	}

	public void setEstado(EstadoSesion estado) {
		this.estado = estado;
	}

	public Valoracion getValoracion() {
		return valoracion;
	}

	public void setValoracion(Valoracion valoracion) {
		this.valoracion = valoracion;
	}

	public String getUrlJitsi() {
		return urlJitsi;
	}

	public void setUrlJitsi(String urlJitsi) {
		this.urlJitsi = urlJitsi;
	}

	public List<ObjectError> getErrores() {
		return errores;
	}

	public void setErrores(List<ObjectError> errores) {
		this.errores = errores;
	}

	public Sesion(Usuario usuario, Asesor asesor, LocalDateTime fechaHora, EstadoSesion estado, Valoracion valoracion,
			String urlJitsi, List<ObjectError> errores) {
		this.usuario = usuario;
		this.asesor = asesor;
		this.fechaHora = fechaHora;
		this.estado = estado;
		this.valoracion = valoracion;
		this.urlJitsi = urlJitsi;
		this.errores = errores;
	}

	public Sesion() {

	}

	
}
