package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
public class FormulaPbs  {

    @JsonProperty( "numero" )
    private String numero;

    @JsonProperty( "tipo" )
    private String tipo;

    @JsonProperty( "codigoTipo" )
    private String codigoTipo;

    @JsonProperty( "nombreTipo" )
    private String nombreTipo;

    @JsonProperty( "autorizacion" )
    private String autorizacion;

    @JsonProperty( "preautorizacion" )
    private String preautorizacion;

    @JsonProperty( "vigencia" )
    private String vigencia;

    @JsonProperty( "exento" )
    private String exento;

    @JsonProperty( "fechaSolicitud" )
    private String fechaSolicitud;

    @JsonProperty( "fechaServicio")
    private String fechaServicio;
    
    @JsonProperty( "fechaVencimiento" )
    private String fechaVencimiento;

    @JsonProperty( "diagnostico" )
    private Diagnostico diagnostico;

    @JsonProperty( "aseguradora" )
    private Aseguradora aseguradora;

    @JsonProperty( "cobro" )
    private Cobro cobro;

    @JsonProperty( "ips" )
    private Ips ips;

    @JsonProperty( "prestador" )
    private PrestadorIps prestador;

    @JsonProperty( "tratamiento" )
    private List<TreatmentData> treatmentData;

    @JsonProperty( "dispensacion" )
    private DispensacionFormula dispensacion;
}
