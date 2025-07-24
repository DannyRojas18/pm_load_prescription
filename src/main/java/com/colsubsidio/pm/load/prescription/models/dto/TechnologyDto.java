package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnologyDto { 
    
    @JsonProperty("tipo")
    private String type;
    
    @JsonProperty("codigo")
    private String code;
}
