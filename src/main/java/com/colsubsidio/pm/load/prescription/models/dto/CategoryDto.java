package com.colsubsidio.pm.load.prescription.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
public class CategoryDto {

	@JsonProperty("tipo")
	private String type;

	@JsonProperty("codigo")
	private String code;
	
	@JsonProperty("nombre")
	private String name;
	
	@JsonProperty("ips")
	private String ips;
	
	
	@JsonProperty("exencion")
	private String exemption;
	
	
	
	
}
