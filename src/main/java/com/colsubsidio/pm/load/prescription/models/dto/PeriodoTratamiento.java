package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PeriodoTratamiento implements Serializable {

    @JsonProperty("cantidad")
    private String cantidad;

    @JsonProperty("unidad")
    private String unidad;

    private static final long serialVersionUID = 1L;

    public PeriodoTratamiento() {
    	
    }
    
    public PeriodoTratamiento(PeriodoTratamiento pt) {
    	if(null != pt) {
    		this.cantidad = pt.getCantidad();
        	this.unidad = pt.getUnidad();
    	}
    }
}
