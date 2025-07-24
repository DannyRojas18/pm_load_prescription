/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.models.dto;

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
public final class ResultDto {
    private int code;
    private String description;
}
