package com.EduConnectB.app.models;

import jakarta.persistence.*;
import java.time.YearMonth;

@Entity
@Table(name = "informe")
public class Informe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInforme;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario estudiante;

    private YearMonth mesAnio;
    private String contenido;


    public Informe(Usuario estudiante, YearMonth mesAnio, String contenido) {
        this.estudiante = estudiante;
        this.mesAnio = mesAnio;
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

    public YearMonth getMesAnio() {
        return mesAnio;
    }

    public void setMesAnio(YearMonth mesAnio) {
        this.mesAnio = mesAnio;
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