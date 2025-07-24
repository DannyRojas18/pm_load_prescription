package com.colsubsidio.pm.load.prescription.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.colsubsidio.pm.load.prescription.enums.ECategory;
import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EFieldValidatorState;
import com.colsubsidio.pm.load.prescription.enums.EFormulaState;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionSource;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionStatus;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.pm.load.prescription.enums.ETypePrescription;
import com.colsubsidio.pm.load.prescription.models.dao.EpsDao;
import com.colsubsidio.pm.load.prescription.models.dao.FieldValidatorDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionOIDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentOIDao;
import com.colsubsidio.pm.load.prescription.models.entities.Eps;
import com.colsubsidio.pm.load.prescription.models.entities.FieldValidator;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionOI;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentOI;
import com.colsubsidio.pm.load.prescription.utilities.Utils;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.date.DateUtils;
import com.colsubsidio.utilities.miscellaneous.enumeration.EDateFormat;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author dchavarro
 * @version 1.0
 * Clase encargada de realizar los procesos de OI 
 *
 */
@Component
@RequiredArgsConstructor
public class PrescriptionHelperOI {

	private final LogsManager log;
	private final TreatmentOIDao treatmentDao;
	private final MedicalPrescriptionOIDao medicalPrescriptionDao;
	private final FieldValidatorDao fieldValidatorDao;
    private final PMPublishExchange pMPublishExchange;
    private final Utils utils;
    
    /**
     * Metodo encargado de actualizar el tratamiento
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param treatment
     */
    public void updateTreatmentAddressing( JsonElement jsonElement, TreatmentOI treatment ) {

    	log.info("Inicio metodo updateTreatmentAddressing: {} ", treatment);
        JsonElement element,elementAnnulment;

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
        elementAnnulment = JsonUtil.getElementFromPath( jsonElement, "FecAnulacion" );
        if( !elementAnnulment.isJsonNull() ) {
            dateAnnulment = JsonUtil.getElementAsString( elementAnnulment );
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
        this.treatmentDao.save( treatment );
        log.info("Fin metodo updateTreatmentAddressing: {} ", treatment);
    }
    
    /**
     * Metodo encargado de crear la prescripción del medico
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param eps
     */
    public void createMedicalPrescriptionAddressing( JsonElement jsonElement, EEps eps ) {
    	log.info("Inicio metodo createMedicalPrescriptionAddressing: {} ", eps);

        MedicalPrescriptionOI medicalPrescription;

        JsonElement element;

        String nit, numberIdPatient, typeIdPatient, mipresNumber, epsCode;
        Integer idSeat;

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
        
        //* Obtenemos el id de la sede
        element = JsonUtil.getElementFromPath( jsonElement, "formula.dispensacion.aseguradora.codigo" );
        log.trace("element aseguradora primero: {} ", element);
        idSeat = Integer.parseInt(JsonUtil.getElementAsString( element ));
        
        Eps entitiesEps = this.utils.getEpsToInsurance(String.valueOf(JsonUtil.getElementAsString( element )));

        medicalPrescription = new MedicalPrescriptionOI();
        medicalPrescription.setIdPrescriptionSource( EPrescriptionSource.MIPRES.getId() );
        medicalPrescription.setMipresNumber( mipresNumber );
        medicalPrescription.setIdEps(Objects.nonNull(entitiesEps)  ? entitiesEps.getId() : eps.getIdEps() );
        medicalPrescription.setEpsNit(Objects.nonNull(entitiesEps)  ? entitiesEps.getNit() :nit );
        medicalPrescription.setEpsCode(Objects.nonNull(entitiesEps)  ? entitiesEps.getCodigo() : epsCode );
        medicalPrescription.setTypeIdPatient( typeIdPatient );
        medicalPrescription.setNumberIdPatient( numberIdPatient );
        medicalPrescription.setPrescriptionType( ETypePrescription.NOPBS.getId() );
        medicalPrescription.setIdSeat(idSeat);//Id sede

        medicalPrescription = this.medicalPrescriptionDao.save( medicalPrescription );

        createTreatmentAddressing( jsonElement, medicalPrescription );
        log.info("Fin metodo createMedicalPrescriptionAddressing: {} ", eps);
    }
    
    /**
     * Metodo encargado de crear el tratamiento de la prescripción
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param medicalPrescription
     */
    public void createTreatmentAddressing( JsonElement jsonElement, MedicalPrescriptionOI medicalPrescription ) {
    	log.info("Inicio metodo createTreatmentAddressing: {} ", medicalPrescription);
    	
        TreatmentOI treatment;

        JsonElement element, elementAnnulment = null;

        Gson gson;

        String deliveryNumber, maxDeliveryDate, prescriptionNumber, idMedicalPrescription, idAddressing;
        String mipresId, typeTechnology, consecutiveTechnology,
            addressingDate, codeTechnology, dateAnnulment = null;

        gson = new Gson();
        idMedicalPrescription = medicalPrescription.getIdMedicalPrescription();

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

        treatment = new TreatmentOI();
        treatment.setIdMedicalPrescription( idMedicalPrescription );
        treatment.setNumberPrescription( prescriptionNumber );
        treatment.setAttempts( 0 );
        treatment.setAddressingId( Integer.parseInt( idAddressing ) );
        treatment.setDeliveryNumber( deliveryNumber );
        treatment.setMipresId( mipresId );
        treatment.setAddressingMetaData( gson.toJson( jsonElement ) );
        treatment.setTypeTechnology( typeTechnology );
        treatment.setConsecutiveTechnology( consecutiveTechnology );
        treatment.setMaxDeliveryDate( DateUtils.getStringToDate( maxDeliveryDate,
                                                                 EDateFormat.ISO_8601_SHORT.getFormat() ) );
        treatment.setAddressingDate( DateUtils.getStringToDate( addressingDate,
                                                                EDateFormat.ISO_8601_SHORT.getFormat() ) );

        treatment.setCodeTechnology( codeTechnology );
        treatment.setIdPrescriptionStatus( EPrescriptionStatus.CONSULTADA.getId() );
        treatment.setIdFormulaState( EFormulaState.NO_ENTREGADO.getId() );
        treatment.setVoidDate( DateUtils.getStringToDate( dateAnnulment,
                                                          EDateFormat.ISO_8601_SHORT.getFormat() ) );
        this.treatmentDao.save( treatment );
        log.info("Fin metodo createTreatmentAddressing: {} ", medicalPrescription);
    }
    
    /**
     * Metodo encargado de realizar split de las prescripciones del tramiento
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param prescription
     * @return Devuelve la lista de elementos
     */
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
    
    /**
     * Metodo encargado de actualizar tratamiento de metadata
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param treatment
     * @throws Exception
     */
    public void updateTreatmentFromMetadata( JsonElement jsonElement, TreatmentOI treatment ) throws Exception {

        JsonElement element;

        Gson gson;

        Integer currentFormulaState;

        String prescriptionNumber, preauthorization, authorizationNumber, cum, materialId, consecutiveTechnology;

        currentFormulaState = treatment.getIdFormulaState();

        gson = new Gson();

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        prescriptionNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el número de autorizacion
        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorizationNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el número de la pre-autorizacion.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
        cum = JsonUtil.getElementAsString( element );

        //* Obtenemos el numero del material cliente.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );
    
        element = JsonUtil.getElementFromPath( jsonElement,"formula.tratamiento[0].orden");
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        treatment.setNumberPrescription( prescriptionNumber );
        
        if(Strings.isNullOrEmpty(authorizationNumber)){
            authorizationNumber = treatment.getAuthorizationNumber();
        }

        if(Strings.isNullOrEmpty(preauthorization)){
            preauthorization = treatment.getPreauthorizationNumber();
        }
    
        if(!StringUtils.hasLength( treatment.getCodeTechnology() ) && StringUtils.hasLength( consecutiveTechnology ) ){
            treatment.setCodeTechnology( consecutiveTechnology );
        }

        treatment.setPreauthorizationNumber( preauthorization );
        treatment.setAuthorizationNumber( authorizationNumber );
        treatment.setIdFormulaState( currentFormulaState );

        treatment.setCum( cum );
        treatment.setMaterialId( materialId );

        //* Se cambia el estado de la fórmula solo si está en NO ENTREGADO, de lo contrario se deja el que está.
        if( EFormulaState.NO_ENTREGADO.getId().equals( currentFormulaState ) ) {
            
            treatment.setEpsMetadata( gson.toJson( jsonElement ) );
        }
        else {
            log.trace( "Status will not changed because the formula state is differente of NOT DELIVERY. " +
                       "Id: ", treatment.getIdTreatment() );
        }

        treatment =  this.treatmentDao.save( treatment );

        log.info( "Se encola la informacion de PROCESS_INFO_USER_FAMI {} ",
        		treatment.getIdTreatment() );

        this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_FAMI,
        		treatment.getIdTreatment());
    }
    
    /**
     * Metodo encargar crear la prescripción
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param eps
     * @throws Exception
     */
    public void createMedicalPrescription( JsonElement jsonElement, EEps eps ) throws Exception {

        MedicalPrescriptionOI medicalPrescription;

        JsonElement element;

        String nit, numberIdPatient, typeIdPatient, mipresNumber;
        Integer idSeat;

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "paciente.documento.tipo" );
        typeIdPatient = JsonUtil.getElementAsString( element );

        //* Obtenemo el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "paciente.documento.numero" );
        numberIdPatient = JsonUtil.getElementAsString( element );

        //* Obtenemo el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
        mipresNumber = JsonUtil.getElementAsString( element );

        if(mipresNumber != null && mipresNumber.toLowerCase().equals("null")){
            mipresNumber = null;
        }

        //* Obtenemos el NIT de la aseguradora
        element = JsonUtil.getElementFromPath( jsonElement, "aseguradora.nit" );
        nit = JsonUtil.getElementAsString( element );
        
      //* Obtenemos el id de la sede
        element = JsonUtil.getElementFromPath( jsonElement, "formula.dispensacion.aseguradora.codigo" );
        log.trace("element aseguradora segundo: {} ", element);
        idSeat = Integer.parseInt(JsonUtil.getElementAsString( element ));

        Eps entitiesEps = this.utils.getEpsToInsurance(String.valueOf(JsonUtil.getElementAsString( element )));
        
        medicalPrescription = new MedicalPrescriptionOI();
        medicalPrescription.setEpsNit( Objects.nonNull(entitiesEps)  ? entitiesEps.getNit() :nit );
        medicalPrescription.setMipresNumber( mipresNumber );
        medicalPrescription.setIdPrescriptionSource( EPrescriptionSource.OI.getId() );
        medicalPrescription.setIdEps( Objects.nonNull(entitiesEps)  ? entitiesEps.getId() : eps.getIdEps());
        medicalPrescription.setMipresNumber( mipresNumber );
        medicalPrescription.setTypeIdPatient( typeIdPatient );
        medicalPrescription.setNumberIdPatient( numberIdPatient );
        medicalPrescription.setPrescriptionType( mipresNumber == null ? ETypePrescription.PBS.getId() :
                                                 ETypePrescription.NOPBS.getId() );
        medicalPrescription.setIdSeat(idSeat);

        medicalPrescription = this.medicalPrescriptionDao.save( medicalPrescription );

        createTreatment( jsonElement, medicalPrescription );
    }

    /**
     * Metodo encargado de crear el tratamiento
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param jsonElement
     * @param medicalPrescription
     * @throws Exception
     */
    public void createTreatment( JsonElement jsonElement, MedicalPrescriptionOI medicalPrescription ) throws Exception {
        TreatmentOI treatment;

        EFormulaState formulaState;

        JsonElement element;

        Gson gson;

        String deliveryNumber, cum, status, maxDeliveryDate, prescriptionNumber, idMedicalPrescription;
        String preauthorization, authorization, materialId;

        gson = new Gson();
        idMedicalPrescription = medicalPrescription.getIdMedicalPrescription();

        //* Obtenemos el número de entrega.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].numeroEntrega" );
        deliveryNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el cum del tratamiento.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
        cum = JsonUtil.getElementAsString( element );

        //* Obtenemos el estado de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.status" );
        status = JsonUtil.getElementAsString( element );

        //* Obtenemos la fecha de vencimiento.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.fechaVencimiento" );
        maxDeliveryDate = JsonUtil.getElementAsString( element );

        if( Strings.isNullOrEmpty( maxDeliveryDate ) ) {

            maxDeliveryDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        }

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        prescriptionNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        //* Obtenemos el número de la fórmula.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorization = JsonUtil.getElementAsString( element );

        //* Obtenemos el numero del material cliente.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );

        treatment = new TreatmentOI();
        treatment.setIdMedicalPrescription( idMedicalPrescription );
        treatment.setNumberPrescription( prescriptionNumber );
        treatment.setPreauthorizationNumber( preauthorization );
        treatment.setAuthorizationNumber( authorization );
        treatment.setAttempts( 0 );

        treatment.setCum( cum );
        treatment.setDeliveryNumber( deliveryNumber );
        treatment.setMaterialId( materialId );
        treatment.setEpsMetadata( gson.toJson( jsonElement ) );
        treatment.setMaxDeliveryDate( DateUtils.getStringToDate( maxDeliveryDate,
                                                                 EDateFormat.ISO_8601_SHORT.getFormat() ) );

        treatment.setIdPrescriptionStatus( EPrescriptionStatus.CONSULTADA.getId() );

        formulaState = transformStatusOIToFormulaState( status );

        if( formulaState != null ) {
            treatment.setIdFormulaState( formulaState.getId() );
        }
        else {
            treatment.setIdFormulaState( EFormulaState.NO_ENTREGADO.getId() );
        }
        treatment =  this.treatmentDao.save( treatment );

        log.info( "Se encola la informacion de PROCESS_INFO_USER_FAMI {} ",
        		treatment.getIdTreatment() );

        this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_FAMI,
        		treatment.getIdTreatment());
       
    }
    
    /**
     * Metodo encarga de transformar el estado
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param status
     * @return Devuelve el estado de la formula
     */
    public EFormulaState transformStatusOIToFormulaState( String status ) {

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
}
