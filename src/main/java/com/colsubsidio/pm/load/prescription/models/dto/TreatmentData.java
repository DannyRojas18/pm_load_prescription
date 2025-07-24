package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonIgnoreProperties
public class TreatmentData {

    @JsonProperty( "numeroEntrega" )
    private String numeroEntrega;

    @JsonProperty( "producto" )
    private Producto producto;

    @JsonProperty( "cantidad" )
    private String cantidad;

    @JsonProperty( "pago" )
    private PagoTratamiento pago;

    @JsonProperty( "dosis" )
    private DosisTramamiento dosis;

    @JsonProperty( "fechaVencimiento" )
    private String fechaVencimiento;

    @JsonProperty( "estado" )
    private String estado;

    public Integer getCantEntrega() {
    	return Integer.parseInt(this.numeroEntrega);
    }
    

    public TreatmentData() {
    	
    }
    
    public TreatmentData( TreatmentData t) {
    	this.numeroEntrega = t.getNumeroEntrega();
    	this.producto = new Producto(t.getProducto());
    	this.cantidad = t.getCantidad();
    	this.pago = new PagoTratamiento(t.getPago());
    	this.dosis = new DosisTramamiento(t.getDosis());
    	this.fechaVencimiento = t.getFechaVencimiento();
    	this.estado = t.getEstado();
    }
    
}
