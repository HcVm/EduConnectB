package com.EduConnectB.app.dto;

import java.time.LocalDateTime;

public class SesionDTO {
    private Integer idSesion;
    private LocalDateTime fechaHora;
    private AsesorDTO asesor;
    private ValoracionDTO valoracion;
	public Integer getIdSesion() {
		return idSesion;
	}
	public void setIdSesion(Integer idSesion) {
		this.idSesion = idSesion;
	}
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}
	public AsesorDTO getAsesor() {
		return asesor;
	}
	public void setAsesor(AsesorDTO asesor) {
		this.asesor = asesor;
	}
	public ValoracionDTO getValoracion() {
		return valoracion;
	}
	public void setValoracion(ValoracionDTO valoracion) {
		this.valoracion = valoracion;
	}
	public SesionDTO(Integer idSesion, LocalDateTime fechaHora, AsesorDTO asesor, ValoracionDTO valoracion) {
		this.idSesion = idSesion;
		this.fechaHora = fechaHora;
		this.asesor = asesor;
		this.valoracion = valoracion;
	}
	public SesionDTO() {

	}
    
}
