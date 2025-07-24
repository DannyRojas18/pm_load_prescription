/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Nathaly Gutierrez
 */
@Data
public class PatientNotAuthorized implements Serializable{        
  
    @JsonProperty("documento")
    private Document documento;    
    @JsonProperty("nombre")
    private NameNotAuthorized nombre;     
    @JsonProperty("edad")
    private Long edad;    
    @JsonProperty("genero")
    private String genero; 
    @JsonProperty("ips")
    private IpsNotAuthorized ips;
    @JsonProperty("regimen")
    private Regime regimen;
    @JsonProperty("condicion")
    private ConditionPatient condicion;
    
}
