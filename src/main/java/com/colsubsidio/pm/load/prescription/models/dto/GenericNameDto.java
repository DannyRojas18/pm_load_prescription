package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Alberto Palencia Benedetti
 */
@Data
@Builder
public class GenericNameDto {
    
    @JsonProperty("nombre")
    private String name;
    
    @JsonProperty("codigo")
    private String code;
}
