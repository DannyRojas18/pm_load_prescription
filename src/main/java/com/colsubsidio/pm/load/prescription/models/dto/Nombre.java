package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Nombre implements Serializable {
    private static final long serialVersionUID = -984232992941938475L;
    
    @JsonProperty( "primero" )
    private String primero;

    @JsonProperty( "segundo" )
    private String segundo;

    @JsonProperty( "primerApellido" )
    private String primerApellido;

    @JsonProperty( "segundoApellido" )
    private String segundoApellido;
}
