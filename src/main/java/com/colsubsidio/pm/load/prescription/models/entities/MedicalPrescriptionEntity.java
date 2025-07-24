package com.colsubsidio.pm.load.prescription.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MedicalPrescriptionEntity {

    private String idMedicalPrescription;

    private Integer idPrescriptionSource;

    private String mipresNumber;

    private Integer idEps;

    private String epsNit;

    private String epsCode;

    private Integer prescriptionType;
    
    private String typeIdPatient;

    private String numberIdPatient;

}
