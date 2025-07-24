package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IpsDto {
    
    @JsonProperty("codigo")
    private String code;
    
    @JsonProperty("nombre")
    private BusinessName BusinessName;

    @JsonProperty("sucursal")
    private String branchCode;
    
    @JsonProperty("secundaria")
    private IpsDto secondaryIps;
    
}
