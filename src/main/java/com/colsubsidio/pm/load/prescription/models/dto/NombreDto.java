package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NombreDto {
    
    @JsonProperty("primero")
    private String first;
    
    @JsonProperty("primerApellido")
    private String firstLastName;
    
    @JsonProperty("segundoApellido")
    private String secondLastName;
}
