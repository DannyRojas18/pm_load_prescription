
package com.colsubsidio.pm.load.prescription.models.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewbornDto {
	@JsonProperty("nombre")
	private NombreDto newbornName;

	@JsonProperty("documento")
	private DocumentDto document;
	

	@JsonProperty("esRecienNacido")
	private Boolean isNewBorn;

	@JsonProperty("claseRegistro")
	private String RecordClass;
}
