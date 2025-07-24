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
public class DispensacionFormula   {


    @JsonProperty("sucursal")
    private String sucursal;

    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("aseguradora")
    private AseguradoraDispensacionFormula aseguradoraFormula;
    
}
