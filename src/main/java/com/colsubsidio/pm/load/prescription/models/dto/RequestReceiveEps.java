package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class RequestReceiveEps {

    @JsonProperty( "paciente" )
    private Paciente paciente;

    @JsonProperty( "formula" )
    private FormulaPbs formula;
}