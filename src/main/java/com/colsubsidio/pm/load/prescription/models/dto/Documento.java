package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Documento implements Serializable {

    @JsonProperty( "tipo" )
    private String tipo;

    @JsonProperty( "numero" )
    private String numero;

    private static final long serialVersionUID = 1L;

}
