package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class DuracionTratamiento implements Serializable {

    @JsonProperty("cantidad")
    private String cantidad;
    
    @JsonProperty("unidad")
    private String unidad;

    private static final long serialVersionUID = 1L;

    public DuracionTratamiento() {
    	
    }
    
    public DuracionTratamiento(DuracionTratamiento dt) {
    	if(null != dt) {
    		this.cantidad = dt.getCantidad();
        	this.unidad = dt.getUnidad();
    	}
    }
    
}
