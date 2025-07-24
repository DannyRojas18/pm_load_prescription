/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.models.dto;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Robert Barraza
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class ResponseDto {
    private ArrayList<ResultDto> result;
}
