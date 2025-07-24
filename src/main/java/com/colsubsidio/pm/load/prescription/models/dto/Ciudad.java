package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Ciudad implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nombre")
    private String nombre;

    private static final long serialVersionUID = 1L;

}
