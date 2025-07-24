package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UnidadTratamiento implements Serializable {
    private static final long serialVersionUID = 3454323522768042990L;

    @JsonProperty( "nombre" )
    private String nombre;
    
    @JsonProperty( "cantidad" )
    private String cantidad;
    
    public UnidadTratamiento() {
    	
    }
    
    public UnidadTratamiento(UnidadTratamiento ut) {
    	if(null != ut) {
    		this.nombre = ut.getNombre();
        	this.cantidad = ut.getCantidad();
    	}
    }
}
