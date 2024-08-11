package com.EduConnectB.app.dto;

import java.util.List;

public class MembresiasResponse {
	private Long cantidadMembresias;
    private List<MembresiaDTO> membresias;
    
	public Long getCantidadMembresias() {
		return cantidadMembresias;
	}
	public void setCantidadMembresias(Long cantidadMembresias) {
		this.cantidadMembresias = cantidadMembresias;
	}
	public List<MembresiaDTO> getMembresias() {
		return membresias;
	}
	public void setMembresias(List<MembresiaDTO> membresias) {
		this.membresias = membresias;
	}
	public MembresiasResponse(Long cantidadMembresias, List<MembresiaDTO> membresias) {
		this.cantidadMembresias = cantidadMembresias;
		this.membresias = membresias;
	}
	public MembresiasResponse() {

	}

    
}
