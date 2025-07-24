package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PatienDto {
    
    @JsonProperty("documento")
    private DocumentDto document;
    
    @JsonProperty("nombre")
    private NombreDto patientName;
    
    @JsonProperty("direccion")
    private AddressDto address;
    
    @JsonProperty("genero")
    private String gender;
    
    @JsonProperty("fechaNacimiento")
    private String birthDate;
    
    @JsonProperty("direccionPrincipal")
    private String mainAddress;
    
    @JsonProperty("telefono")
    private String phone;
    
    @JsonProperty("celular")
    private String cellPhone;
    
    @JsonProperty("correoElectronico")
    private String email;
    
    @JsonProperty("regimen")
    private RegimenDto regimen;
    
    @JsonProperty("ips")
    private IpsDto ips;
    
    @JsonProperty("categoria")
    private CategoryDto CategoryDto;
    
    
    @JsonProperty("observaciones")
    private List<ObservationDto> observations;
}
