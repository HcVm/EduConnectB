package com.EduConnectB.app.models;

import jakarta.persistence.*;

@Entity
@Table(name = "informe")
public class Informe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInforme;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario estudiante;
    private String contenido;


    public Informe(Usuario estudiante, String contenido) {
        this.estudiante = estudiante;
        this.contenido = contenido;
    }

    public Integer getIdInforme() {
        return idInforme;
    }
    
    public void setIdInforme(Integer idInforme) {
        this.idInforme = idInforme;
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
	public Informe() {
	}
    
    
}
