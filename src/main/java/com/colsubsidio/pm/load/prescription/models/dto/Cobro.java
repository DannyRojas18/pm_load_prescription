package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Cobro implements Serializable {

    @JsonProperty( "cuotaModeradora" )
    private CuotaModeradora cuotaModeradora;

    @JsonProperty( "copago" )
    private Copago copago;

    private static final long serialVersionUID = 1L;

}
