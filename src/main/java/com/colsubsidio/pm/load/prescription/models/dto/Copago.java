package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Alberto Palencia Benedetti
 */
public class Copago {

    @JsonProperty( "valor" )
    private String valor;

    @JsonProperty( "porcentaje" )
    private String porcentaje;
}
