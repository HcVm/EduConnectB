package com.EduConnectB.app.dto;

import com.EduConnectB.app.models.TipoMembresia;

public class CompraMembresiaRequest {
	
	private Integer usuarioId;
    private TipoMembresia tipoMembresia;
    private String tokenTemporal;
    private DatosPago datosPago;
    
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
	
	public String getTokenTemporal() {
        return tokenTemporal;
    }

    public void setTokenTemporal(String tokenTemporal) {
        this.tokenTemporal = tokenTemporal;
    }
	
    public DatosPago getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(DatosPago datosPago) {
        this.datosPago = datosPago;
    }
}
