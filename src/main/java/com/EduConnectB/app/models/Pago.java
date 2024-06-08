package com.EduConnectB.app.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "id_membresia")
    private Membresia membresia;

    private BigDecimal monto;
    private LocalDateTime fecha;
	public Integer getIdPago() {
		return idPago;
	}
	public void setIdPago(Integer idPago) {
		this.idPago = idPago;
	}
	public Membresia getMembresia() {
		return membresia;
	}
	public void setMembresia(Membresia membresia) {
		this.membresia = membresia;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public Pago(Membresia membresia, BigDecimal monto, LocalDateTime fecha) {
		this.membresia = membresia;
		this.monto = monto;
		this.fecha = fecha;
	}
	public Pago() {

	}
    
    
}
