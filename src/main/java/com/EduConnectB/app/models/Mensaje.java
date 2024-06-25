package com.EduConnectB.app.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_remitente")
    private Usuario remitente;

    @ManyToOne
    @JoinColumn(name = "id_destinatario")
    private Usuario destinatario;

    private String contenido;
    private LocalDateTime fechaHora;
	public Integer getIdMensaje() {
		return idMensaje;
	}
	public void setIdMensaje(Integer idMensaje) {
		this.idMensaje = idMensaje;
	}
	public Usuario getRemitente() {
		return remitente;
	}
	public void setRemitente(Usuario remitente) {
		this.remitente = remitente;
	}
	public Usuario getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(Usuario destinatario) {
		this.destinatario = destinatario;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}
	public Mensaje(Usuario remitente, Usuario destinatario, String contenido, LocalDateTime fechaHora) {
		this.remitente = remitente;
		this.destinatario = destinatario;
		this.contenido = contenido;
		this.fechaHora = fechaHora;
	}
	public Mensaje() {

	}
    
    
}
