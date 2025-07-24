package com.colsubsidio.pm.load.prescription.models.services.interfaces;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionEntity;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentEntity;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/29/2020
 */
public interface IBuilderEntity {
    void saveMedicalPrescription( MedicalPrescriptionEntity medicalPrescription );
    void saveTreatment( TreatmentEntity treatment );
}
