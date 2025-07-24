package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessDto {

	@JsonProperty("tipo")
    private String tyoe;
    
    @JsonProperty("codigo")
    private String code;
    
    @JsonProperty("vigencia")
    private String daysValidity;
    
    @JsonProperty("altoCosto")
    private String highPrice;
    
    
    @JsonProperty("catastrofica")
    private String catastrophic;
    
    @JsonProperty("capitado")
    private String capitulated;
    
    @JsonProperty("disponibles")
    private String avalibles;
    
    @JsonProperty("fechaIngreso")
    private String admissionDate;
	
}
