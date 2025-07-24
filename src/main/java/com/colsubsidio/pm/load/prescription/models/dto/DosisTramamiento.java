package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DosisTramamiento implements Serializable {
    private static final long serialVersionUID = 4818594094311152452L;

    @JsonProperty( "unidad" )
    private UnidadTratamiento unidad;

    @JsonProperty( "periodo" )
    private PeriodoTratamiento periodo;

    @JsonProperty( "duracion" )
    private DuracionTratamiento duracion;
    
    public DosisTramamiento() {
    	
    }
    
    public DosisTramamiento(DosisTramamiento dt) {
    	if(null != dt) {
    		this.unidad = new UnidadTratamiento(dt.getUnidad());
        	this.periodo = new PeriodoTratamiento(dt.getPeriodo());
        	this.duracion = new DuracionTratamiento(dt.getDuracion());
    	}
    }
}
