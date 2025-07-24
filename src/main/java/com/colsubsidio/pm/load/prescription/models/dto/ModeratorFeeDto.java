package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ModeratorFeeDto {

	@JsonProperty("valor")
	private String value;
	
	@JsonProperty("porcentaje")
	private String percentage;
	
	@JsonProperty("valorMaximo")
	private String maxValue;
	
}
