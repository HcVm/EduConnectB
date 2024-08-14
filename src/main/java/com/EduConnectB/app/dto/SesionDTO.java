package com.EduConnectB.app.dto;

import java.time.LocalDateTime;

public class SesionDTO {
    private Integer idSesion;
    private LocalDateTime fechaHora;
    private AsesorDTO asesor;
    
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
	public SesionDTO(Integer idSesion, LocalDateTime fechaHora, AsesorDTO asesor) {
		this.idSesion = idSesion;
		this.fechaHora = fechaHora;
		this.asesor = asesor;
	}
	public SesionDTO() {

	}
  
}
