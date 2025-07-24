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
public class Patient implements Serializable{
        
    @JsonProperty("nombre")
    private Name nombre;    
    @JsonProperty("documento")
    private Document documento;
    @JsonProperty("categoria")
    private Category categoria;
    @JsonProperty("edad")
    private String edad;
    @JsonProperty("genero")
    private String genero;    
    @JsonProperty("fechaNacimiento")
    private String fechaNacimiento;
    @JsonProperty("estado")
    private StatePatient estado;   
    @JsonProperty("semanasCotizadas")
    private String semanasCotizadas;
    @JsonProperty("clasificacion")
    private Classification clasificacion;
    @JsonProperty("ips")
    private Ips ips;
    @JsonProperty("direccionPrincipal")
    private String direccionPrincipal;   
    @JsonProperty("direccion")
    private Address direccion;
    @JsonProperty("telefono")
    private String telefono;
    
}
