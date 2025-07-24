package com.colsubsidio.pm.load.prescription.models.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/20/2020
 * Description: Clase para retornar el status de la validacion de la formula
 */

@Getter
@Setter
public class ValidationFormulaResult {

    @JsonProperty("codigo")
    private String code;

    @JsonProperty("descripcion")
    private String description;
}
