/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Nathaly Gutierrez
 */
@Data
@JsonIgnoreProperties
public class Ips implements Serializable{
    
    @JsonProperty("codigo")
    private String codigo;
    @JsonProperty("nombre")
    private String nombre;  
    @JsonProperty("sucursal")
    private String sucursal;  
    
}
