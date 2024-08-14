package com.EduConnectB.app.dto;

public class ValoracionDTO {
	private Integer idValoracion;
    private Integer puntuacion;
    private String comentario;
    private SesionDTO sesion;
    
	public Integer getIdValoracion() {
		return idValoracion;
	}
	public void setIdValoracion(Integer idValoracion) {
		this.idValoracion = idValoracion;
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
	public SesionDTO getSesion() {
		return sesion;
	}
	public void setSesion(SesionDTO sesion) {
		this.sesion = sesion;
	}
	public ValoracionDTO(Integer idValoracion, Integer puntuacion, String comentario, SesionDTO sesion) {
		this.idValoracion = idValoracion;
		this.puntuacion = puntuacion;
		this.comentario = comentario;
		this.sesion = sesion;
	}
	public ValoracionDTO() {

	}

}
