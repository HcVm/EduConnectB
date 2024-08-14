package com.EduConnectB.app.dto;

public class AsesorDTO {
    private Integer idAsesor;
    private String nombre; 
    private String especialidad;
    
	public Integer getIdAsesor() {
		return idAsesor;
	}
	public void setIdAsesor(Integer idAsesor) {
		this.idAsesor = idAsesor;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
	public AsesorDTO(Integer idAsesor, String nombre, String especialidad) {
		this.idAsesor = idAsesor;
		this.nombre = nombre;
		this.especialidad = especialidad;
	}
	public AsesorDTO() {

	}
    
}