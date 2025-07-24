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
public class ProductNotAuthorized implements Serializable{    
    
    @JsonProperty( "mapis" )
    private Long mapis;
    @JsonProperty( "descripcion" )
    private String descripcion;
    @JsonProperty( "cum" )
    private String cum;
    @JsonProperty( "concentracion" )
    private String concentracion;
}
