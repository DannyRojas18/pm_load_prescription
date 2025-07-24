package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormulaDto {

	@JsonProperty("numero")
	private String number;

	@JsonProperty("mipres")
	private String mipres;

	@JsonProperty("preautorizacion")
	private String preauthorization;

	@JsonProperty("autorizacion")
	private String authorization;

	@JsonProperty("fechaVencimiento")
	private String expirationDate;

	@JsonProperty("fechaServicio")
	private String serviceDate;

	@JsonProperty("fechaSolicitud")
	private String serviceRequest;

	@JsonProperty("observaciones")
	private String observations;
	
	@JsonProperty("estado")
	private String state;

	@JsonProperty("covid")
	private String covid;

	@JsonProperty("cobro")
	private PaymentDto payment;
	
	@JsonProperty("servicio")
	private ServiceDto service;


	@JsonProperty("diagnostico")
	private Diagnosis diagnosis;
	
	@JsonProperty("razonSocial")
	private String businessName;
	
	
	@JsonProperty("contratacion")
	private String recruitment;
	
	@JsonProperty("ips")
	private IpsDto ips;

	@JsonProperty("prestador")
	private ProviderDto provider;

	@JsonProperty("tratamiento")
	private List<TreatmentDto> treatment;
}
