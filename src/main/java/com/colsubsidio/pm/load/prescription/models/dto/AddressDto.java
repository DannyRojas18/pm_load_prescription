package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    
    @JsonProperty("direccionPrincipal")
    private String mainAddress;
    
    @JsonProperty("ciudad")
    private GenericNameDto city;
    
    @JsonProperty("departamento")
    private GenericNameDto departament;
}
