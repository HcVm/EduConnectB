package com.EduConnectB.app.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "calificacion")
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCalificacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String nombreMateria;
    private BigDecimal calificacion;
    private LocalDate fecha;
    private String comentario;
    
    @ManyToOne
    @JoinColumn(name = "id_sesion")
    private Sesion sesion;

	public Integer getIdCalificacion() {
		return idCalificacion;
	}

	public void setIdCalificacion(Integer idCalificacion) {
		this.idCalificacion = idCalificacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNombreMateria() {
		return nombreMateria;
	}

	public void setNombreMateria(String nombreMateria) {
		this.nombreMateria = nombreMateria;
	}

	public BigDecimal getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(BigDecimal calificacion) {
		this.calificacion = calificacion;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Sesion getSesion() {
		return sesion;
	}

	public void setSesion(Sesion sesion) {
		this.sesion = sesion;
	}

	public Calificacion(Usuario usuario, String nombreMateria, BigDecimal calificacion, LocalDate fecha,
			String comentario, Sesion sesion) {
		this.usuario = usuario;
		this.nombreMateria = nombreMateria;
		this.calificacion = calificacion;
		this.fecha = fecha;
		this.comentario = comentario;
		this.sesion = sesion;
	}

	public Calificacion() {
	}
    
    
}
