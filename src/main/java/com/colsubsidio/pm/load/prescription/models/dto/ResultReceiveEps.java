package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Ingeneo
 */
@Data
public class ResultReceiveEps {
    @JsonProperty( "codigo" )
    private String codigo;

    @JsonProperty( "descripcion" )
    private String descripcion;
}