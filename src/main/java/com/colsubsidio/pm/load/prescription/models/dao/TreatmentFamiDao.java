package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.TreatmentFami;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 11/19/2020 Description:
 */
public interface TreatmentFamiDao extends CrudRepository<TreatmentFami, String> {

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.TreatmentFami AS T WITH ( NOLOCK ) " +
                    "WHERE T.IdPrescriptionStatus = ?1 AND " +
                    "      T.AddressingId IS NOT NULL" )
    List<TreatmentFami> findByTreatmentStatusIdPending( Integer prescriptionStatusId );

    @Query( nativeQuery = true,
            value = "SELECT TOP 1 * " +
                    "FROM PM.TreatmentFami AS TN WITH ( NOLOCK ) " +
                    "     LEFT JOIN PM.DeliveryFami AS DN WITH ( NOLOCK ) " +
                    "       ON TN.IdTreatmentFami = DN.IdTreatmentFami " +
                    "WHERE TN.MaxDeliveryDate >= ?2 AND TN.MipresId = ?1 " +
                    "ORDER BY TN.DateMaxDelivery Asc" )
    Optional<TreatmentFami> searchTop1ByPrescriptionIdAndDateMaxDelivery( String number, String requestDate );

    @Query( nativeQuery = true,
            value = "SELECT  TN.* " +
                    "FROM PM.MedicalPrescriptionFami AS MPN WITH( NOLOCK ) " +
                    "     JOIN PM.TreatmentFami AS TN  WITH( NOLOCK ) " +
                    "       ON TN.IdMedicalPrescriptionFami = MPN.IdMedicalPrescriptionFami " +
                    "WHERE MPN.MipresNumber = ?1 AND TN.MipresId = ?2 " )
    Optional<TreatmentFami> findByMipresNumberAndPrescriptionId( String mipresNumber, String mipresId );

    /// ERROR DE CONSULTA
    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT T.* " +
                    "FROM PM.TreatmentFami T " +
                    "     JOIN PM.IdMedicalPrescriptionFami MP " +
                    "       ON T.IdMedicalPrescription = MP.IdMedicalPrescription " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      T.DeliveryNumber = :DeliveryNumber AND " +
                    "      ( T.CUM = :CUM OR " +
                    "        PM.ExtractFirstPart( T.CodeTechnology, :Separator ) = " +
                    "          PM.ExtractFirstPart( :CUM, :Separator ) )" )
    Optional<TreatmentFami> findTreatmentByMipresDeliveryCum( @Param( "MipresNumber" ) String mipresNumber,
                                                              @Param( "DeliveryNumber" ) String deliveryNumber,
                                                              @Param( "CUM" ) String cum,
                                                              @Param( "Separator" ) String separator );

    Optional<TreatmentFami> findByNumberPrescriptionAndCum( String mipresNumber, String cum );

    Optional<TreatmentFami> findByNumberPrescriptionAndMaterialId( String mipresNumber, String materialId );

    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.TreatmentFami TN WITH ( NOLOCK ) " +
                    "WHERE TN.IdMedicalPrescriptionFami = :IdMedicalPrescription AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber AND " +
                    "      TN.IdFormulaState <> :IdFormulaState" )
    Optional<TreatmentFami> findTreamentDifferentToState( @Param( "IdMedicalPrescription" ) String idMedicalPrescription,
                                                          @Param( "TypeTechnology" ) String typeTechnology,
                                                          @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                          @Param( "DeliveryNumber" ) String deliveryNumber,
                                                          @Param( "IdFormulaState" ) Integer idFormulaState );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = " SELECT T.* " +
                    " FROM  PM.TreatmentFami AS T " +
                    " JOIN PM.MedicalPrescriptionFami AS M" +
                    "        ON M.idMedicalPrescriptionFami = T.idMedicalPrescriptionFami " +
                    " WHERE M.MipresNumber = :mipresNumber AND T.MipresId = :mipresId" )
    public Optional<TreatmentFami> findByMipressNumberAndMipresId( @Param( "mipresNumber" ) String mipresNumber,
                                                                   @Param( "mipresId" ) String mipresId );

    @Query( nativeQuery = true,
            value = "SELECT TN.*" +
                    "FROM PM.TreatmentFami TN WITH ( NOLOCK ) " +
                    "     JOIN PM.MedicalPrescriptionFami MP " +
                    "       ON TN.IdMedicalPrescriptionFami = MP.IdMedicalPrescriptionFami " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber " )
    Optional<TreatmentFami> findTreamentFamiEps( @Param( "MipresNumber" ) String mipresNumber,
                                                 @Param( "TypeTechnology" ) String typeTechnology,
                                                 @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                 @Param( "DeliveryNumber" ) String deliveryNumber );

    @Query( nativeQuery = true,
            value = " SELECT  TF.* " +
                    " FROM PM.TreatmentFami TF ( NOLOCK ) " +
                    " WHERE TF.NumberPrescription = :NumberPrescription " )
    List<TreatmentFami> findByNumberPrescription( @Param( "NumberPrescription" ) String numberPrescription );

    @Query( nativeQuery = true,
            value = " SELECT  TOP 1 TF.* " +
                    " FROM PM.TreatmentFami TF ( NOLOCK ) " +
                    " WHERE TF.NumberPrescription = :NumberPrescription AND " +
                    "       TF.MaterialId = :MaterialId " )
    Optional<TreatmentFami> findByNumberPrescription( @Param( "NumberPrescription" ) String numberPrescription,
                                                      @Param( "MaterialId" ) String MaterialId );

}
