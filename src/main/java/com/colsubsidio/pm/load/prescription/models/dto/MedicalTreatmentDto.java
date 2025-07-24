package com.colsubsidio.pm.load.prescription.models.dto;
/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/1/2020
 * Description:
 */

import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalTreatmentDto {
     
     private String mipresNumber;
     private String idTreatment;
     private String typeTechnology;
     private String consecutiveTechnology;
     private String deliveryNumber;
     private Date   createDate;
     private String typeIdPatient;
     private String numberIdPatient;
     private JsonArray metadata;
}
