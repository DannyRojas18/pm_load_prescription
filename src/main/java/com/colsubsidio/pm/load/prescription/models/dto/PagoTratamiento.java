package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class PagoTratamiento implements Serializable {

    @JsonProperty("valor")
    private String valor;

    @JsonProperty("porcentaje")
    private String porcentaje;

    private static final long serialVersionUID = 1L;

    public PagoTratamiento() {
    	
    }
    
    public PagoTratamiento(PagoTratamiento pt) {
    	if(null != pt) {
    		this.valor = pt.getValor();
    		this.porcentaje = pt.getPorcentaje();
    	}
    }
    
}
