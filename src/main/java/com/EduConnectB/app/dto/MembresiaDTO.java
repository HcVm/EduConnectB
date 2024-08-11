package com.EduConnectB.app.dto;

import java.time.LocalDate;

import com.EduConnectB.app.models.TipoMembresia;

public class MembresiaDTO {
	
	private Integer idMembresia;
    private TipoMembresia tipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
	public Integer getIdMembresia() {
		return idMembresia;
	}
	public void setIdMembresia(Integer idMembresia) {
		this.idMembresia = idMembresia;
	}
	public TipoMembresia getTipoMembresia() {
		return tipoMembresia;
	}
	public void setTipoMembresia(TipoMembresia tipoMembresia) {
		this.tipoMembresia = tipoMembresia;
	}
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public LocalDate getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	public MembresiaDTO(Integer idMembresia, TipoMembresia tipoMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
		this.idMembresia = idMembresia;
		this.tipoMembresia = tipoMembresia;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}
		
	public MembresiaDTO() {

	}

}
