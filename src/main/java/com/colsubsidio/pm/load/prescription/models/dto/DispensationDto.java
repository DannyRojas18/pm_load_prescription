package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DispensationDto {
    
    @JsonProperty("sucursal")
    private String branchOffice;
    
    @JsonProperty("codigo")
    private String code;
    
    @JsonProperty("subCodigo")
    private String subCode;
    
    
}
