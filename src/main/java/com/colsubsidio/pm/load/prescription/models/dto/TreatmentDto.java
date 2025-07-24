package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("orden")
    private String order;
    
    @JsonProperty("preautorizacion")
    private String preauthorizacion;
    
    @JsonProperty("fechaVencimiento")
    private String expirationDate;
    
    @JsonProperty("fechaServicio")
    private String serviceDate;
    
    @JsonProperty("estado")
    private String status;
    
    @JsonProperty("numeroEntrega")
    private String deliveryNumber;
    
    @JsonProperty("producto")
    private ProductDto product;
    
    @JsonProperty("procedimiento")
    private ProcessDto ProcessDto;
    
    @JsonProperty("dosis")
    private DoseDto dose;
    
    @JsonProperty("cantidad")
    private String quantity;
    
    @JsonProperty("diasVigencia")
    private String daysValidity;
    
    @JsonProperty("dispensacion")
    private DispensationDto dispensationDto;
}
