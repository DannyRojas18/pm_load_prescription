package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Ingeneo
 */
@Data
@JsonIgnoreProperties
public class PrestadorIps  {

    @JsonProperty( "codigo" )
    private String codigo;

    @JsonProperty( "nombre" )
    private Nombre nombre;

    @JsonProperty( "documento" )
    private Documento documento;

    @JsonProperty( "registro" )
    private String registro;

    @JsonProperty( "especialidad" )
    private String especialidad;
}
