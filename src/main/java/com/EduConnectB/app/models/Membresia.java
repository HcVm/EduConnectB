package com.EduConnectB.app.models;
import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "membresia")
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMembresia;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoMembresia tipoMembresia;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
	public Integer getIdMembresia() {
		return idMembresia;
	}
	public void setIdMembresia(Integer idMembresia) {
		this.idMembresia = idMembresia;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	public Membresia(Integer idMembresia, Usuario usuario, TipoMembresia tipoMembresia, LocalDate fechaInicio,
			LocalDate fechaFin) {
		this.idMembresia = idMembresia;
		this.usuario = usuario;
		this.tipoMembresia = tipoMembresia;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}
	public Membresia() {

	}
     
	
}
