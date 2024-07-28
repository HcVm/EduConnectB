package com.EduConnectB.app.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "archivos_asesor")
public class ArchivoAsesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_asesor", nullable = false)
    @JsonBackReference
    private Asesor asesor;

    private String nombreArchivo;
    private String rutaArchivo;
    private LocalDateTime fechaSubida;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Asesor getAsesor() {
		return asesor;
	}
	public void setAsesor(Asesor asesor) {
		this.asesor = asesor;
	}
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public String getRutaArchivo() {
		return rutaArchivo;
	}
	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}
	public LocalDateTime getFechaSubida() {
		return fechaSubida;
	}
	public void setFechaSubida(LocalDateTime fechaSubida) {
		this.fechaSubida = fechaSubida;
	}
	public ArchivoAsesor(Asesor asesor, String nombreArchivo, String rutaArchivo, LocalDateTime fechaSubida) {
		this.asesor = asesor;
		this.nombreArchivo = nombreArchivo;
		this.rutaArchivo = rutaArchivo;
		this.fechaSubida = fechaSubida;
	}
	public ArchivoAsesor() {

	}

}
