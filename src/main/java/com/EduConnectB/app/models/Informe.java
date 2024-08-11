package com.EduConnectB.app.models;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "informe")
public class Informe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInforme;
    private Timestamp fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario estudiante;
   
    private String contenido;
    
	public Integer getIdInforme() {
		return idInforme;
	}
	public void setIdInforme(Integer idInforme) {
		this.idInforme = idInforme;
	}
	public Timestamp getFecha() {
		return fecha;
	}
	public void setFecha(Timestamp timestamp) {
		this.fecha = timestamp;
	}
	public Usuario getEstudiante() {
		return estudiante;
	}
	public void setEstudiante(Usuario estudiante) {
		this.estudiante = estudiante;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public Informe(Timestamp fecha, Usuario estudiante, String contenido) {
		this.fecha = fecha;
		this.estudiante = estudiante;
		this.contenido = contenido;
	}
	public Informe() {

	}

}
