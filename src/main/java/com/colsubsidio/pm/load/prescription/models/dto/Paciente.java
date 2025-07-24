package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Paciente {
    @JsonProperty("documento")
    private Documento documento;

    @JsonProperty("nombre")
    private Nombre nombre;

    @JsonProperty("grupo")
    private String grupo;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("diagnostico")
    private Diagnostico diagnostico;

    @JsonProperty("grupoEtareo")
    private String grupoEtareo;

    @JsonProperty("programa")
    private String programa;

    @JsonProperty("fechaNacimiento")
    private String fechaNacimiento;

    @JsonProperty("genero")
    private String genero;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("direccion")
    private Direccion direccion;

    @JsonProperty("correoElectronico")
    private String correoElectronico;

    @JsonProperty("afiliacion")
    private String afiliacion;

    @JsonProperty("ips")
    private Ips ips;
}
