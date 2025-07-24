package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CuotaModeradora implements Serializable {

    @JsonProperty("valor")
    private  String valor;

    private static final long serialVersionUID = 1L;
}
