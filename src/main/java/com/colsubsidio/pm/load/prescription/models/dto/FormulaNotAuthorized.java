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
public class FormulaNotAuthorized implements Serializable {

    @JsonProperty( "numero" )
    private Long numero;
    @JsonProperty( "fechaServicio" )
    private String fechaServicio;
    @JsonProperty( "orden" )
    private Long orden;
    @JsonProperty( "actividad" )
    private String actividad;
    @JsonProperty( "categoria" )
    private CategoryNotAuthorized categoria;
    @JsonProperty( "servicio" )
    private Service servicio;
    @JsonProperty( "cantidad" )
    private Long cantidad;
    @JsonProperty( "diagnostico" )
    private DiagnosisNotAuthorized diagnostico;
    @JsonProperty( "ips" )
    private IpsNotAuthorized ips;
    @JsonProperty( "cobro" )
    private Payment cobro;
    @JsonProperty( "prestador" )
    private Provider prestador;

    @JsonProperty( "tratamiento" )
    private TreatmentNotAuthorized[] tratamiento;

}
