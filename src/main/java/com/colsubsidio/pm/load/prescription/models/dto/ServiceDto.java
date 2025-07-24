package com.colsubsidio.pm.load.prescription.models.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceDto {
    
    @JsonProperty("nombre")
    private String name;
    
    @JsonProperty("codigo")
    private String code;
}
