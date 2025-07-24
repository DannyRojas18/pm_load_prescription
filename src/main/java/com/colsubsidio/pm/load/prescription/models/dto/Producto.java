package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Producto implements Serializable {
    private static final long serialVersionUID = -739269430741663689L;
    private static final String EXP = "^0+(?!$)";
    
    @JsonProperty( "mapis" )
    private String mapis;

    @JsonProperty( "descripcion" )
    private String descripcion;

    @JsonProperty( "cum" )
    private String cum;

    @JsonProperty( "cantidadDeEntregas" )
    private String cantidadDeEntregas;
    
    public Producto() {
    	
    }
    
    public Producto(Producto p) {
    	if(null != p) {
    		this.mapis = p.getMapis().replaceFirst(EXP, "");
    		this.cum = p.getCum();
    		this.descripcion = p.getDescripcion();
    		this.cantidadDeEntregas = p.getCantidadDeEntregas();
    	}
    }
}