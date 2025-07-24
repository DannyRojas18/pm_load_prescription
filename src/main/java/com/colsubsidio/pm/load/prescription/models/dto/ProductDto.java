package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    
    @JsonProperty("codigo")
    private String code;
    
    @JsonProperty("mapis")
    private String mapis;
    
    @JsonProperty("descripcion")
    private String description;
    
    @JsonProperty("cum")
    private String cum;
    
    @JsonProperty("tecnologia")
    private TechnologyDto technology;
}
