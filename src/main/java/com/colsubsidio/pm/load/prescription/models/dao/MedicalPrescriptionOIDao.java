package com.colsubsidio.pm.load.prescription.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionOI;


public interface MedicalPrescriptionOIDao extends CrudRepository<MedicalPrescriptionOI, String>{
	
	@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
	    @Query(nativeQuery = true,
	            value = "SELECT  " +
	                            " MP.MipresNumber," +
	                            " TN.IdTreatment AS IdTreatment," +
	                            " TN.TypeTechnology, " +
	                            " TN.ConsecutiveTechnology , " +
	                            " TN.DeliveryNumber, " +
	                            " MP.TypeIdPatient, " +
	                            " MP.NumberIdPatient, " +
	                            " MP.CreateDate " +
	                            "FROM PM.MedicalPrescriptionOI AS MP ( NOLOCK ) " +
	                            "     JOIN PM.TreatmentOI AS TN ( NOLOCK ) " +
	                            "       ON MP.IdMedicalPrescription = TN.IdMedicalPrescription " +
	                            "WHERE MP.MipresNumber IS NOT NULL AND " +
	                            "      TN.UpdateDate >= DATEADD( MINUTE, :minutes * -1, DATEADD( HOUR, -5, GETDATE() ) ) AND " +
	                            "      TN.EpsMetadata IS NULL AND TN.IdFormulaState <> 2 " +
	                            "  ORDER BY MP.MipresNumber ")
	    List<MedicalTreatmentResult> findPrescriptionPendingNoPBS( @Param( "minutes" ) Integer minutes );

	@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
	    @Query( nativeQuery = true,
	            value = "SELECT MP.* " +
	                    "FROM PM.MedicalPrescriptionOI MP WITH( NOLOCK ) " +
	                    "     JOIN PM.TreatmentOI T WITH( NOLOCK ) " +
	                    "       ON MP.IdMedicalPrescription = T.IdMedicalPrescription " +
	                    "WHERE T.NumberPrescription = :numberPrescription" )
	    Optional<MedicalPrescriptionOI> findByNumberPrescription( @Param( "numberPrescription" ) String numberPrescription );
}
