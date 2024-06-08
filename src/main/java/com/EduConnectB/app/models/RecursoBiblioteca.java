package com.EduConnectB.app.models;


public class RecursoBiblioteca {
    private String id;
    private String titulo;
    private String tipo;
    private String contenido;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public RecursoBiblioteca(String titulo, String tipo, String contenido) {
		this.titulo = titulo;
		this.tipo = tipo;
		this.contenido = contenido;
	}
	public RecursoBiblioteca() {

	}
	
    
}