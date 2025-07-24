package com.colsubsidio.pm.load.prescription.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.colsubsidio.pm.load.prescription.models.entities.TreatmentOI;

public interface TreatmentOIDao extends CrudRepository<TreatmentOI, String> {

	@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
	@Query(nativeQuery = true, value = " SELECT T.* " + " FROM  PM.TreatmentOI AS T "
			+ " JOIN PM.MedicalPrescriptionOI AS M"
			+ "        ON M.idMedicalPrescription = T.idMedicalPrescription "
			+ " WHERE M.MipresNumber = :mipresNumber AND T.MipresId = :mipresId")
	public Optional<TreatmentOI> findByMipressNumberAndMipresId(@Param("mipresNumber") String mipresNumber,
			@Param("mipresId") String mipresId);
	
	@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
	@Query( nativeQuery = true,
            value = "SELECT TN.*" +
                    "FROM PM.TreatmentOI TN WITH ( NOLOCK ) " +
                    "     JOIN PM.MedicalPrescriptionOI MP " +
                    "       ON TN.IdMedicalPrescription = MP.IdMedicalPrescription " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber " )
    Optional<TreatmentOI> findTreamentOIEps( @Param( "MipresNumber" ) String mipresNumber,
                                                 @Param( "TypeTechnology" ) String typeTechnology,
                                                 @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                 @Param( "DeliveryNumber" ) String deliveryNumber );
	
   Optional<TreatmentOI> findByNumberPrescriptionAndCum( String mipresNumber, String cum );
   
   Optional<TreatmentOI> findByNumberPrescriptionAndMaterialId( String mipresNumber, String materialId );
   
   @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
   @Query( nativeQuery = true,
           value = " SELECT  TF.* " +
                   " FROM PM.TreatmentOI TF ( NOLOCK ) " +
                   " WHERE TF.NumberPrescription = :NumberPrescription " )
   List<TreatmentOI> findByNumberPrescription( @Param( "NumberPrescription" ) String numberPrescription );
   
   @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
   @Query( nativeQuery = true,
           value = " SELECT  TOP 1 TF.* " +
                   " FROM PM.TreatmentOI TF ( NOLOCK ) " +
                   " WHERE TF.NumberPrescription = :NumberPrescription AND " +
                   "       TF.MaterialId = :MaterialId " )
   Optional<TreatmentOI> findByNumberPrescription( @Param( "NumberPrescription" ) String numberPrescription,
                                                     @Param( "MaterialId" ) String MaterialId );

}
