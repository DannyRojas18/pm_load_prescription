package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {

	@JsonProperty("codigo")
	private String code;
	
	@JsonProperty("tipo")
	private String type;
	
	@JsonProperty("nombre")
	private String name;
	
	@JsonProperty("estado")
	private String state;
	
	@JsonProperty("valor")
	private String value;
	
	@JsonProperty("porcentajeEPS")
	private String EPSpercentage;
	
	@JsonProperty("cuotaModeradora")
	private ModeratorFeeDto ModeratorFeeDto;
	
	@JsonProperty("copago")
	private CopayDto copayDto;
	
}
