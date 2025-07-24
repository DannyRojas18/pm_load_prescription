package com.colsubsidio.pm.load.prescription.models.entities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/29/2020
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreatmentEntity {
    
    private String numberPrescription;
    
    private String mipresId;
    
    private String preauthorizationNumber;
    
    private String authorizationNumber;
    
    private String typeTechnology;
    
    private String consecutiveTechnology;
    
    private String deliveryNumber;
    
    private String codeTechnology;
    
    private String cum;
    
    private Integer addressingId;
    
    private String programationId;
    
    private String deliveryReportId;
    
    private Integer idPrescriptionStatus;
    
    private Date maxDeliveryDate;
    
    private String addressingMetaData;
    
    private String epsMetadata;
    
    private Integer attempts;
    
    private String codeBranchProvisioning;
    
    private Date addressingDate;
    
    private Integer idFormulaState;
    
    private String materialId;
    
    private Date voidDate;
   
}
