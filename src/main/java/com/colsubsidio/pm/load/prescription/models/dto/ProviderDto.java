package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderDto {
    
    @JsonProperty("codigo")
    private String code;
    
    @JsonProperty("documento")
    private DocumentDto document;
    
    @JsonProperty("direccion")
    private AddressDto addressDto;
    
    @JsonProperty("nombre")
    private NombreDto providerName;
}
