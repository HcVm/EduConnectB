package com.EduConnectB.app.models;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "asesor")
public class Asesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAsesor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String especialidad;
    
    @Column(columnDefinition = "json")
    private String horarioDisponibilidad;
    
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ArchivoAsesor> archivos = new ArrayList<>();

	public Integer getIdAsesor() {
		return idAsesor;
	}

	public void setIdAsesor(Integer idAsesor) {
		this.idAsesor = idAsesor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getHorarioDisponibilidad() {
		return horarioDisponibilidad;
	}

	public void setHorarioDisponibilidad(String horarioDisponibilidad) {
		this.horarioDisponibilidad = horarioDisponibilidad;
	}

	public List<ArchivoAsesor> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<ArchivoAsesor> archivos) {
		this.archivos = archivos;
	}

	public Asesor(Usuario usuario, String especialidad, String horarioDisponibilidad, List<ArchivoAsesor> archivos) {
		this.usuario = usuario;
		this.especialidad = especialidad;
		this.horarioDisponibilidad = horarioDisponibilidad;
		this.archivos = archivos;
	}

	public Asesor() {

	}

}
