package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Direccion implements Serializable {

    @JsonProperty( "direccionPrincipal" )
    private String direccionPrincipal;

    @JsonProperty( "departamento" )
    private Ciudad departamento;

    @JsonProperty( "ciudad" )
    private Ciudad ciudad;

    @JsonProperty( "pais" )
    private Ciudad pais;

    private static final long serialVersionUID = 1L;

}
