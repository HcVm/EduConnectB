package com.EduConnectB.app.dto;

import java.time.LocalDateTime;

public class SolicitudSesionRequest {
    private Integer idAsesor;
    private LocalDateTime fechaHora;
    
	public Integer getIdAsesor() {
		return idAsesor;
	}
	public void setIdAsesor(Integer idAsesor) {
		this.idAsesor = idAsesor;
	}
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}
    
    
}
