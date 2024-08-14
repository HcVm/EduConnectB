package com.EduConnectB.app.dto;

public class ValoracionDTO {
    private Integer idValoracion;
    private Integer puntuacion;
    private String comentario;
    
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
	public ValoracionDTO(Integer idValoracion, Integer puntuacion, String comentario) {

		this.idValoracion = idValoracion;
		this.puntuacion = puntuacion;
		this.comentario = comentario;
	}
	public ValoracionDTO() {

	}
	
}
