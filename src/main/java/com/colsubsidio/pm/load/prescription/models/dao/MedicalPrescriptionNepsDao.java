package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionNeps;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interface de implementacion CrudRepository
 *
 * @author Ingeneo
 *
 */
public interface MedicalPrescriptionNepsDao extends CrudRepository<MedicalPrescriptionNeps, String> {

	@Transactional(readOnly = true)
	@Query(nativeQuery = true,
//            value = "SELECT  " +
//                     " MP.MipresNumber," +
//                     " TN.IdTreatmentNeps AS IdTreatment," +
//                     " TN.TypeTechnology, " +
//                     " TN.ConsecutiveTechnology , " +
//                     " TN.DeliveryNumber " +
//                    "FROM PM.MedicalPrescriptionNeps AS MP ( NOLOCK ) " +
//                    "     JOIN PM.TreatmentNeps AS TN ( NOLOCK ) " +
//                    "       ON MP.IdMedicalPrescriptionNeps = TN.IdMedicalPrescriptionNeps " +
//                    "WHERE MP.MipresNumber IS NOT NULL AND " +
//                    "      TN.UpdateDate >= DATEADD( MINUTE, :minutes * -1, DATEADD( HOUR, -5, GETDATE() ) ) AND " +
//                    "      TN.EpsMetadata IS NULL " +
//                    "  ORDER BY MP.MipresNumber "
			value = "EXEC PM.SP_PRESCRIPTION_PENDING_NOPBS_NEPS ?1")
	List<MedicalTreatmentResult> findPrescriptionPendingNoPBS(@Param("minutes") Integer minutes);

	@Transactional(readOnly = true)
	@Query(nativeQuery = true, value = "SELECT MP.* " + "FROM PM.MedicalPrescriptionNeps MP WITH( NOLOCK ) "
			+ "     JOIN PM.TreatmentNeps T WITH( NOLOCK ) "
			+ "       ON MP.IdMedicalPrescriptionNeps = T.IdMedicalPrescriptionNeps "
			+ "WHERE T.NumberPrescription = :numberPrescription")
	Optional<MedicalPrescriptionNeps> findByNumberPrescription(@Param("numberPrescription") String numberPrescription);

}
