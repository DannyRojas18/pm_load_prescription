package com.colsubsidio.pm.load.prescription.models.dao;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionCoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/22/2020
 */
public interface MedicalPrescriptionCoomDao extends CrudRepository<MedicalPrescriptionCoom, String> {
    @Transactional( readOnly = true )
    @Query(nativeQuery = true,
           value = " SELECT  " +
                   "  MPC.MipresNumber," +
                   "  TC.IdTreatmentCoomeva AS IdTreatment ," +
                   "  TC.TypeTechnology, " +
                   "  TC.ConsecutiveTechnology , " +
                   "  TC.DeliveryNumber " +
                   " FROM PM.MedicalPrescriptionCoom AS MPC ( NOLOCK ) " +
                   "     JOIN PM.TreatmentCoom AS TC ( NOLOCK ) " +
                   "       ON MPC.IdMedicalPrescriptionCoomeva = TC.IdMedicalPrescriptionCoomeva " +
                   " WHERE MPC.MipresNumber IS NOT NULL AND " +
                   "       TC.UpdateDate >= DATEADD( MINUTE, :minutes * -1, DATEADD( HOUR, -5, GETDATE() ) ) AND " +
                   "       TC.EpsMetadata IS NULL AND TC.IdFormulaState <> 2 " +
                   " ORDER BY MPC.MipresNumber ")
    List<MedicalTreatmentResult> findPrescriptionPendingNoPBS( @Param( "minutes" ) Integer minutes );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT MP.* " +
                    "FROM PM.MedicalPrescriptionCoom MP WITH( NOLOCK ) " +
                    "     JOIN PM.TreatmentCoom T WITH( NOLOCK ) " +
                    "       ON MP.IdMedicalPrescriptionCoomeva = T.IdMedicalPrescriptionCoomeva " +
                    "WHERE T.NumberPrescription = :numberPrescription" )
    Optional<MedicalPrescriptionCoom> findByNumberPrescription( @Param( "numberPrescription" ) String numberPrescription );
}
