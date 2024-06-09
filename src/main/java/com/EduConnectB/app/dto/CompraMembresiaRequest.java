package com.EduConnectB.app.dto;

import com.EduConnectB.app.models.TipoMembresia;

public class CompraMembresiaRequest {
	
	private Integer usuarioId;
    private TipoMembresia tipoMembresia;
    
	public Integer getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(Integer usuarioId) {
		this.usuarioId = usuarioId;
	}
	public TipoMembresia getTipoMembresia() {
		return tipoMembresia;
	}
	public void setTipoMembresia(TipoMembresia tipoMembresia) {
		this.tipoMembresia = tipoMembresia;
	}   

}
