package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoseDto {
    
    @JsonProperty("indicacionesEspeciales")
    private String specialIndications;
    
    @JsonProperty("regimen")
    private String regimen;
}
