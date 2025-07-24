package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionFami;
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
public interface MedicalPrescriptionFamiDao extends CrudRepository<MedicalPrescriptionFami, String> {

	@Transactional(readOnly = true)
	@Query(nativeQuery = true,
//            value = "SELECT  " +
//                            " MP.MipresNumber," +
//                            " TN.IdTreatmentFami AS IdTreatment," +
//                            " TN.TypeTechnology, " +
//                            " TN.ConsecutiveTechnology , " +
//                            " TN.DeliveryNumber, " +
//                            " MP.TypeIdPatient, " +
//                            " MP.NumberIdPatient, " +
//                            " MP.CreateDate " +
//                            "FROM PM.MedicalPrescriptionFami AS MP ( NOLOCK ) " +
//                            "     JOIN PM.TreatmentFami AS TN ( NOLOCK ) " +
//                            "       ON MP.IdMedicalPrescriptionFami = TN.IdMedicalPrescriptionFami " +
//                            "WHERE MP.MipresNumber IS NOT NULL AND " +
//                            "      TN.UpdateDate >= DATEADD( MINUTE, :minutes * -1, DATEADD( HOUR, -5, GETDATE() ) ) AND " +
//                            "      TN.EpsMetadata IS NULL AND TN.IdFormulaState <> 2 " +
//                            "  ORDER BY MP.MipresNumber "
			value = "EXEC PM.SP_PRESCRIPTION_PENDING_NOPBS_FAMI ?1")
	List<MedicalTreatmentResult> findPrescriptionPendingNoPBS(@Param("minutes") Integer minutes);

	@Transactional(readOnly = true)
	@Query(nativeQuery = true, value = "SELECT MP.* " + "FROM PM.MedicalPrescriptionFami MP WITH( NOLOCK ) "
			+ "     JOIN PM.TreatmentFami T WITH( NOLOCK ) "
			+ "       ON MP.IdMedicalPrescriptionFami = T.IdMedicalPrescriptionFami "
			+ "WHERE T.NumberPrescription = :numberPrescription")
	Optional<MedicalPrescriptionFami> findByNumberPrescription(@Param("numberPrescription") String numberPrescription);

}
