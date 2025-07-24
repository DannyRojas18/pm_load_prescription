package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class Convenio implements Serializable {

    @JsonProperty("tipo")
    private String tipo;

    
    private static final long serialVersionUID = 1L;

}