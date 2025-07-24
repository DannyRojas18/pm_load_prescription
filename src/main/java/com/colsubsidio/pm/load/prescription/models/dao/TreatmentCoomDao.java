package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.TreatmentCoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 11/19/2020
 */
public interface TreatmentCoomDao extends CrudRepository<TreatmentCoom, String> {
    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.TreatmentCoom AS T WITH ( NOLOCK ) " +
                    "WHERE T.IdPrescriptionStatus = ?1 AND " +
                    "      T.AddressingId IS NOT NULL" )
    List<TreatmentCoom> findByTreatmentStatusIdPending( Integer prescriptionStatusId );

    @Query( nativeQuery = true,
            value = "SELECT TOP 1 * " +
                    "FROM PM.TreatmentCoom AS TN WITH ( NOLOCK ) " +
                    "     LEFT JOIN PM.DeliveryCoom AS DN WITH ( NOLOCK ) " +
                    "       ON TN.IdTreatmentCoomeva = DN.IdTreatmentCoomeva " +
                    "WHERE TN.MaxDeliveryDate >= ?2 AND TN.MipresId = ?1 " +
                    "ORDER BY TN.DateMaxDelivery Asc" )
    Optional<TreatmentCoom> searchTop1ByPrescriptionIdAndDateMaxDelivery( String number, String requestDate );

    @Query( nativeQuery = true,
            value = "SELECT  TN.* " +
                    "FROM PM.MedicalPrescriptionCoom AS MPN WITH( NOLOCK ) " +
                    "     JOIN PM.TreatmentCoom AS TN  WITH( NOLOCK ) " +
                    "       ON TN.IdMedicalPrescriptionCoomeva = MPN.IdMedicalPrescriptionCoomeva " +
                    "WHERE MPN.MipresNumber = ?1 AND TN.MipresId = ?2 " )
    Optional<TreatmentCoom> findByMipresNumberAndPrescriptionId( String mipresNumber, String mipresId );
    
    /// ERROR DE CONSULTA
    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT T.* " +
                    "FROM PM.TreatmentCoom T " +
                    "     JOIN PM.IdMedicalPrescriptionCoomeva MP " +
                    "       ON T.IdMedicalPrescription = MP.IdMedicalPrescription " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      T.DeliveryNumber = :DeliveryNumber AND " +
                    "      ( T.CUM = :CUM OR " +
                    "        PM.ExtractFirstPart( T.CodeTechnology, :Separator ) = " +
                    "          PM.ExtractFirstPart( :CUM, :Separator ) )" )
    Optional<TreatmentCoom> findTreatmentByMipresDeliveryCum( @Param( "MipresNumber" ) String mipresNumber,
                                                                     @Param( "DeliveryNumber" ) String deliveryNumber,
                                                                     @Param( "CUM" ) String cum,
                                                                     @Param( "Separator" ) String separator );

    Optional<TreatmentCoom> findByNumberPrescriptionAndCum( String mipresNumber, String cum );

    Optional<TreatmentCoom> findByNumberPrescriptionAndMaterialId( String mipresNumber, String materialId );
    
    @Query( nativeQuery = true,
            value = "SELECT * " +
                            "FROM PM.TreatmentCoom TN WITH ( NOLOCK ) " +
                            "WHERE TN.IdMedicalPrescriptionCoomeva = :IdMedicalPrescription AND " +
                            "      TN.TypeTechnology = :TypeTechnology AND " +
                            "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                            "      TN.DeliveryNumber = :DeliveryNumber AND " +
                            "      TN.IdFormulaState <> :IdFormulaState" )
    Optional<TreatmentCoom> findTreamentDifferentToState(  @Param( "IdMedicalPrescription" ) String idMedicalPrescription,
                                                            @Param( "TypeTechnology" )  String typeTechnology,
                                                            @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                            @Param( "DeliveryNumber" ) String deliveryNumber,
                                                            @Param( "IdFormulaState" ) Integer idFormulaState );
    
                                                            @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = " SELECT T.* " +
                    " FROM  PM.TreatmentCoom AS T " +
                    " JOIN PM.MedicalPrescriptionCoom AS M" +
                    "        ON M.IdMedicalPrescriptionCoomeva = T.IdMedicalPrescriptionCoomeva " +
                    " WHERE M.MipresNumber = :mipresNumber AND T.MipresId = :mipresId" )
    public Optional<TreatmentCoom> findByMipressNumberAndMipresId( @Param( "mipresNumber" ) String mipresNumber,
                                                                   @Param( "mipresId" ) String mipresId );

    
    @Query( nativeQuery = true,
            value = "SELECT TN.*" +
                    "FROM PM.TreatmentCoom TN WITH ( NOLOCK ) " +
                    "     JOIN PM.MedicalPrescriptionCoom MP " +
                    "       ON TN.IdMedicalPrescriptionCoomeva = MP.IdMedicalPrescriptionCoomeva " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber " )
    Optional<TreatmentCoom> findTreamentCoomEps( @Param( "MipresNumber" ) String mipresNumber,
                                                 @Param( "TypeTechnology" ) String typeTechnology,
                                                 @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                 @Param( "DeliveryNumber" ) String deliveryNumber );
}
