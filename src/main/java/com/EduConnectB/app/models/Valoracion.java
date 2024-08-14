package com.EduConnectB.app.models;
import jakarta.persistence.*;

@Entity
@Table(name = "valoracion")
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idValoracion;

    @OneToOne
    @JoinColumn(name = "sesion_id")
    private Sesion sesion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    private Integer puntuacion;
    private String comentario;
    
	public Integer getIdValoracion() {
		return idValoracion;
	}
	public void setIdValoracion(Integer idValoracion) {
		this.idValoracion = idValoracion;
	}
	public Sesion getSesion() {
		return sesion;
	}
	public void setSesion(Sesion sesion) {
		this.sesion = sesion;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Integer getPuntuacion() {
		return puntuacion;
	}
	public void setPuntuacion(Integer puntuacion) {
		this.puntuacion = puntuacion;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Valoracion(Sesion sesion, Usuario usuario, Integer puntuacion, String comentario) {
		this.sesion = sesion;
		this.usuario = usuario;
		this.puntuacion = puntuacion;
		this.comentario = comentario;
	}
	public Valoracion() {
	}
}
