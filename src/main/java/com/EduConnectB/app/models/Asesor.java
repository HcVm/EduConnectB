package com.EduConnectB.app.models;


import jakarta.persistence.*;

@Entity
@Table(name = "asesor")
public class Asesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAsesor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    private Usuario usuario;

    private String especialidad;
    private String horarioDisponibilidad;
    
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
	public Asesor(Usuario usuario, String especialidad, String horarioDisponibilidad) {
		this.usuario = usuario;
		this.especialidad = especialidad;
		this.horarioDisponibilidad = horarioDisponibilidad;
	}
	public Asesor() {


	}

    
}
