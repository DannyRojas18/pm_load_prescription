package com.colsubsidio.pm.load.prescription.models.dao;
import java.util.Date;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/2/2020
 * Description: Interfaz para obtener la informacion de medical prescription y treatments
 */
public interface MedicalTreatmentResult {
    
    String getMipresNumber();
    String getIdTreatment();
    String getTypeTechnology();
    String getConsecutiveTechnology();
    String getDeliveryNumber();
    Date getCreateDate();
    String getTypeIdPatient();
    String getNumberIdPatient();
    
}
