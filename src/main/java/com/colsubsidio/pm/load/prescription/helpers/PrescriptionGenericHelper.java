package com.colsubsidio.pm.load.prescription.helpers;

import static com.colsubsidio.pm.load.prescription.enums.EFormulaState.NO_ENTREGADO;
import static com.colsubsidio.pm.load.prescription.enums.ETypePrescription.NOPBS;
import static com.colsubsidio.pm.load.prescription.enums.ETypePrescription.PBS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.colsubsidio.pm.load.prescription.enums.ECategory;
import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EFieldValidatorState;
import com.colsubsidio.pm.load.prescription.enums.EFormulaState;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionSource;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionStatus;
import com.colsubsidio.pm.load.prescription.models.dao.FieldValidatorDao;
import com.colsubsidio.pm.load.prescription.models.entities.FieldValidator;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionEntity;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentEntity;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.date.DateUtils;
import com.colsubsidio.utilities.miscellaneous.enumeration.EDateFormat;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import lombok.AllArgsConstructor;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/21/2020 Description:
 */
@AllArgsConstructor
@Component
public class PrescriptionGenericHelper {

    private final LogsManager log;
    private final FieldValidatorDao fieldValidatorDao;

    public List<JsonElement> splitPrescriptionByTreatment( JsonElement prescription ) {
        ArrayList<JsonElement> elementList;

        JsonElement prescriptionTarget, prescriptionClone, element;

        JsonArray treatmentArray;

        int treatmentLength;

        elementList = new ArrayList<>();

        prescriptionClone = JsonUtil.deepCopy( prescription );

        element = JsonUtil.getElementFromPath( prescriptionClone, "formula.tratamiento" ).getAsJsonArray();

        treatmentArray = element != null ? element.getAsJsonArray() : new JsonArray();

        JsonUtil.getElementFromPath( prescriptionClone, "formula" )
            .getAsJsonObject()
            .add( "tratamiento", new JsonArray() );

        treatmentLength = treatmentArray.size();

        for( int i = 0; i < treatmentLength; i++ ) {
            prescriptionTarget = JsonUtil.deepCopy( prescriptionClone );

            JsonUtil.getElementFromPath( prescriptionTarget, "formula.tratamiento" )
                .getAsJsonArray()
                .add( treatmentArray.get( i ) );

            elementList.add( prescriptionTarget );
        }

        return elementList;
    }

    public MedicalPrescriptionEntity createMedicalPrescription( JsonElement jsonElement, EEps eps ) {
    
        MedicalPrescriptionEntity medicalPrescription;

        JsonElement element;

        String nit, numberIdPatient, typeIdPatient, mipresNumber;

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "paciente.documento.tipo" );
        typeIdPatient = JsonUtil.getElementAsString( element );

        //* Obtenemo el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "paciente.documento.numero" );
        numberIdPatient = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
        mipresNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el NIT de la aseguradora
        element = JsonUtil.getElementFromPath( jsonElement, "aseguradora.nit" );
        nit = JsonUtil.getElementAsString( element );

        medicalPrescription = MedicalPrescriptionEntity.builder()
                                                    .epsNit( Strings.isNullOrEmpty( nit ) ? eps.getNit() : nit )
                                                    .mipresNumber( mipresNumber )
                                                    .idPrescriptionSource( EPrescriptionSource.MIPRES.getId() )
                                                    .idEps( eps.getIdEps() )
                                                    .mipresNumber( mipresNumber )
                                                    .typeIdPatient( typeIdPatient )
                                                    .numberIdPatient( numberIdPatient )
                                                    .prescriptionType( Strings.isNullOrEmpty( mipresNumber ) ?
                                                                       PBS.getId() : NOPBS.getId() )
                                                    .build();
    
        return medicalPrescription;
    }

    public TreatmentEntity buildTreatment( JsonElement jsonElement ) {
        TreatmentEntity treatment;
        EFormulaState formulaState;
        JsonElement element;

        Gson gson;

        String deliveryNumber, cum, status, maxDeliveryDate, prescriptionNumber;
        String preauthorization, authorization, materialId;

        gson = new Gson();

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.numeroEntregaActual" );
        deliveryNumber = JsonUtil.getElementAsString( element );
        
        if( Strings.isNullOrEmpty( deliveryNumber ) ) {
            element = JsonUtil.getElementFromPath( jsonElement,"formula.tratamiento[0].numeroEntrega");
            deliveryNumber = JsonUtil.getElementAsString( element );
        }

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
        cum = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.status" );
        status = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.fechaVencimiento" );
        maxDeliveryDate = JsonUtil.getElementAsString( element );

        if( Strings.isNullOrEmpty( maxDeliveryDate ) ) {
            maxDeliveryDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        prescriptionNumber = JsonUtil.getElementAsString( element );
    
        if( !StringUtils.hasLength( prescriptionNumber ) ) {
            element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
            prescriptionNumber = JsonUtil.getElementAsString( element );
        }
        
        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );
    
        if( !StringUtils.hasLength( preauthorization ) ) {
            element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].preautorizacion" );
            preauthorization = JsonUtil.getElementAsString( element );
        }

        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );
        
        formulaState = transformStatusToFormulaState( status );
        
        treatment = TreatmentEntity.builder()
                                   .numberPrescription( prescriptionNumber )
                                   .preauthorizationNumber( preauthorization )
                                   .authorizationNumber( authorization )
                                   .attempts( 0 )
                                   .cum( cum )
                                   .deliveryNumber( deliveryNumber )
                                   .materialId( materialId )
                                   .epsMetadata( gson.toJson( jsonElement ) )
                                   .maxDeliveryDate( DateUtils.getStringToDate( maxDeliveryDate,
                                                                                EDateFormat.ISO_8601_SHORT.getFormat() )
                                   )
                                   .idPrescriptionStatus( EPrescriptionStatus.CONSULTADA.getId() )
                                   .idFormulaState( formulaState != null ? formulaState.getId() : NO_ENTREGADO.getId( ))
                                   .build();
    
        return treatment;
    }

    public EFormulaState transformStatusToFormulaState( String status ) {

        Optional<FieldValidator> optFieldValidator;

        FieldValidator fieldValidator;

        EFormulaState formulaState;

        String changeValue;

        formulaState = null;

        optFieldValidator = this.fieldValidatorDao.findValuesByCategory( status, ECategory.PRESCRIPTION_STATUS_NEPS.getName(),
                                                                         EFieldValidatorState.ACTIVE.getId() );

        if( optFieldValidator.isPresent() ) {
            fieldValidator = optFieldValidator.get();

            changeValue = fieldValidator.getChangeValue();

            if( changeValue != null ) {
                formulaState = EFormulaState.getFormulaStateById( Integer.parseInt( changeValue ) );
            }
        }

        return formulaState;
    }

    
    public void updateTreatmentFromMetadata( JsonElement jsonElement, TreatmentEntity treatment ) {

        JsonElement element;

        Gson gson;

        Integer currentFormulaState;

        String prescriptionNumber, preauthorization, authorizationNumber, cum, materialId;

        currentFormulaState = treatment.getIdFormulaState();

        gson = new Gson();

      
        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        prescriptionNumber = JsonUtil.getElementAsString( element );
        
      
        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorizationNumber = JsonUtil.getElementAsString( element );

      
        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
        cum = JsonUtil.getElementAsString( element );

        
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );

        treatment.setNumberPrescription( prescriptionNumber );
        
        if(Strings.isNullOrEmpty( authorizationNumber ) ){
            authorizationNumber = treatment.getAuthorizationNumber();
        }

        if(Strings.isNullOrEmpty(preauthorization)){
            preauthorization = treatment.getPreauthorizationNumber();
        }

        treatment.setPreauthorizationNumber( preauthorization );
        treatment.setAuthorizationNumber( authorizationNumber );
        treatment.setIdFormulaState( currentFormulaState );

        treatment.setCum( cum );
        treatment.setMaterialId( materialId );
        treatment.setEpsMetadata( gson.toJson( jsonElement ) );
    }

    /*
    public void createMedicalPrescriptionAddressing( JsonElement jsonElement, EEps eps ) {
    
        MedicalPrescriptionEntity medicalPrescription;

        JsonElement element;

        String nit, numberIdPatient, typeIdPatient, mipresNumber, epsCode;

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "TipoIDPaciente" );
        typeIdPatient = JsonUtil.getElementAsString( element );

        //* Obtenemo el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "NoIDPaciente" );
        numberIdPatient = JsonUtil.getElementAsString( element );

        //* Obtenemo el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "NoPrescripcion" );
        mipresNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el NIT de la aseguradora
        element = JsonUtil.getElementFromPath( jsonElement, "NoIDEPS" );
        nit = JsonUtil.getElementAsString( element );

        //* Obtenemos el NIT de la aseguradora
        element = JsonUtil.getElementFromPath( jsonElement, "CodEPS" );
        epsCode = JsonUtil.getElementAsString( element );
    
        medicalPrescription = MedicalPrescriptionEntity.builder()
                                      .idPrescriptionSource( EPrescriptionSource.MIPRES.getId() )
                                      .mipresNumber( mipresNumber )
                                      .idEps( eps.getIdEps() )
                                      .epsNit( nit )
                                      .epsCode( epsCode )
                                      .typeIdPatient( typeIdPatient )
                                      .numberIdPatient( numberIdPatient )
                                      .prescriptionType( NOPBS.getId() )
                                      .build();

        createTreatmentAddressing( jsonElement, idMedicalPrescription );
    }

    public void createTreatmentAddressing( JsonElement jsonElement, String idMedicalPrescription,
        IBuilderEntity builderEntity ) {
        
        TreatmentEntity treatment;
        
        JsonElement element, elementAnnulment ;

        Gson gson;

        String deliveryNumber, maxDeliveryDate, prescriptionNumber,idAddressing;
        String mipresId, typeTechnology, consecutiveTechnology,
            addressingDate, codeTechnology, dateAnnulment = null;

        gson = new Gson();
    
        //* Obtenemos el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "NoEntrega" );
        deliveryNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el IDDireccionamiento.
        element = JsonUtil.getElementFromPath( jsonElement, "IDDireccionamiento" );
        idAddressing = JsonUtil.getElementAsString( element );

        //* Obtenemos el tipo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "TipoTec" );
        typeTechnology = JsonUtil.getElementAsString( element );

        //* Obtenemos el codigo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "ConTec" );
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        //* Obtenemos la fecha de direccionamiento.
        element = JsonUtil.getElementFromPath( jsonElement, "FecDireccionamiento" );
        addressingDate = JsonUtil.getElementAsString( element );

        //* Obtenemos el codigo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "CodSerTecAEntregar" );
        codeTechnology = JsonUtil.getElementAsString( element );

        //* Obtenemos la fecha de vencimiento.
        element = JsonUtil.getElementFromPath( jsonElement, "FecMaxEnt" );

        maxDeliveryDate = JsonUtil.getElementAsString( element );

        if( !Strings.isNullOrEmpty( maxDeliveryDate ) ) {
            maxDeliveryDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        if( !Strings.isNullOrEmpty( addressingDate ) ) {
            addressingDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }
        //* Obtenemos la fecha de anulación.
        elementAnnulment = JsonUtil.getElementFromPath( jsonElement, "FecAnulacion" );
        if( !elementAnnulment.isJsonNull() ) {
            dateAnnulment = JsonUtil.getElementAsString( elementAnnulment );
        }

        if( !Strings.isNullOrEmpty( dateAnnulment ) ) {
            dateAnnulment = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "NoPrescripcion" );
        prescriptionNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el numero del material cliente.
        element = JsonUtil.getElementFromPath( jsonElement, "ID" );
        mipresId = JsonUtil.getElementAsString( element );
    
        treatment =  TreatmentEntity.builder()
                             .idMedicalPrescription( idMedicalPrescription )
                             .numberPrescription( prescriptionNumber )
                             .attempts( 0 )
                             .addressingId( Integer.parseInt( idAddressing ) )
                             .deliveryNumber( deliveryNumber )
                             .mipresId( mipresId )
                             .addressingMetaData( gson.toJson( jsonElement ) )
                             .typeTechnology( typeTechnology )
                             .consecutiveTechnology( consecutiveTechnology )
                             .maxDeliveryDate( DateUtils.getStringToDate( maxDeliveryDate,
                                     EDateFormat.ISO_8601_SHORT.getFormat() ) )
                             .addressingDate( DateUtils.getStringToDate( addressingDate,
                                     EDateFormat.ISO_8601_SHORT.getFormat() ) )
                             .codeTechnology( codeTechnology )
                             .idPrescriptionStatus( EPrescriptionStatus.CONSULTADA.getId() )
                             .idFormulaState( NO_ENTREGADO.getId() )
                             .voidDate( DateUtils.getStringToDate( dateAnnulment,
                                     EDateFormat.ISO_8601_SHORT.getFormat() ) )
                             .build();

        builderEntity.saveTreatment( treatment );
    }

    public void updateTreatmentAddressing( JsonElement jsonElement, TreatmentEntity treatment,
        IBuilderEntity builderEntity ) {

        JsonElement element;

        Gson gson;

        String dateAnnulment = null, idAddressing, addressingDate, maxDeliveryDate, codeTechnology,
            typeTechnology, consecutiveTechnology;

        gson = new Gson();

        //* Obtenemos el IDDireccionamiento.
        element = JsonUtil.getElementFromPath( jsonElement, "IDDireccionamiento" );
        idAddressing = JsonUtil.getElementAsString( element );

        //* Obtenemos la fecha de direccionamiento.
        element = JsonUtil.getElementFromPath( jsonElement, "FecDireccionamiento" );
        addressingDate = JsonUtil.getElementAsString( element );

        //* Obtenemos el tipo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "TipoTec" );
        typeTechnology = JsonUtil.getElementAsString( element );

        if( Strings.isNullOrEmpty( addressingDate ) ) {
            addressingDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }
        //* Obtenemos la fecha de vencimiento.
        element = JsonUtil.getElementFromPath( jsonElement, "FecMaxEnt" );
        maxDeliveryDate = JsonUtil.getElementAsString( element );

        //* Obtenemos el codigo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "ConTec" );
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        //* Obtenemos el codigo de tecnologia.
        element = JsonUtil.getElementFromPath( jsonElement, "CodSerTecAEntregar" );
        codeTechnology = JsonUtil.getElementAsString( element );

        if( Strings.isNullOrEmpty( maxDeliveryDate ) ) {
            maxDeliveryDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

           //* Obtenemos la fecha de anulación.
        element = JsonUtil.getElementFromPath( jsonElement, "FecAnulacion" );
        if( !element.isJsonNull() ) {
            dateAnnulment = JsonUtil.getElementAsString( element );
        }

        if( !Strings.isNullOrEmpty( dateAnnulment ) ) {
            dateAnnulment = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        if( Strings.isNullOrEmpty( dateAnnulment ) ) {
            dateAnnulment = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        treatment.setMaxDeliveryDate( DateUtils.getStringToDate( maxDeliveryDate,
                                                                 EDateFormat.ISO_8601_SHORT.getFormat() ) );
        treatment.setAddressingDate( DateUtils.getStringToDate( addressingDate,
                                                                EDateFormat.ISO_8601_SHORT.getFormat() ) );

        treatment.setAddressingId( Integer.parseInt( idAddressing ) );
        treatment.setTypeTechnology( typeTechnology );
        treatment.setAddressingMetaData( gson.toJson( jsonElement ) );
        treatment.setConsecutiveTechnology( consecutiveTechnology );
        treatment.setVoidDate( DateUtils.getStringToDate( dateAnnulment,
                                                          EDateFormat.ISO_8601_SHORT.getFormat() ) );

        treatment.setCodeTechnology( codeTechnology );
    
        builderEntity.saveTreatment( treatment );
    }
    
     */

}
