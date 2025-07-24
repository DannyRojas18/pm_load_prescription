/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.models.services;

import static java.util.Objects.isNull;

import com.colsubsidio.pm.load.prescription.helpers.*;
import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.pm.load.prescription.enums.EValidationFormula;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentFamiDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentNepsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentOIDao;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentFami;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentNeps;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentOI;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class PrescriptionNoPbsNepsService {

    private final LogsManager log;
    private final PrescriptionHelper prescriptionHelper;
    private final PrescriptionHelperFami prescriptionHelperFami;
    private final TreatmentNepsDao treatmentNepsDao;
    private final TreatmentFamiDao treatmentFamiDao;
    private final TreatmentOIDao treatmentOIDao;
    private final GenericHelper genericHelper;
    private final PMPublishExchange pMPublishExchange;
    private final PrescriptionHelperOI prescriptionHelperOI;

    @Value( "${neps.serviceurl}" )
    private String urlServiceNeps;

    @Value( "${colsubsidio.parameters.url.addressing}" )
    private String urlAddressing;

    public Boolean processPrescription( String numPreauthorization, String numberMipres )
        throws Exception {

        if( !StringUtils.isEmpty( numPreauthorization ) && !StringUtils.isEmpty( numberMipres ) ) {
            log.trace( "Init to processPrescription for numPreauthorization: {} and numberMipres:{} ", numPreauthorization, numberMipres );

            getAddressingPrescription( numberMipres );
            getPrescriptionForLegacy( numPreauthorization, numberMipres );
        }

        return true;
    }

    public void getAddressingPrescription( String numberMipres )
        throws Exception {
        URI uri;

        uri = BasicHelper.buildUri( urlAddressing, numberMipres );

        consultAddressing( uri, numberMipres );
    }

    private void consultAddressing( URI uri, String numberMipres )
        throws Exception {
        ResponseEntity<?> response;

        String body;

        JsonArray prescriptionResponse;

        JsonElement responseJson;

        responseJson = null;

        log.trace( "consultAddressing get from the URI: {}. For EPS NEPS. numberMipres: {}", uri, numberMipres );

        Long initTime, endTime, totalTime;
        initTime = System.currentTimeMillis();
        log.info( "La peticion a {} inicio a las {}", uri, initTime );

        response = this.genericHelper.getService( uri, String.class );
        endTime = System.currentTimeMillis();
        log.info( "La peticion a {} finalizo a las {}", uri, endTime );
        totalTime = endTime - initTime;
        log.info( "El tiempo de respuesta de {} es de {} ms", uri, totalTime );

        body = ( String ) response.getBody();

        if( response.getStatusCode() == HttpStatus.OK && !isNull( body ) ) {
            responseJson = JsonParser.parseString( body );
        }

        if( responseJson != null && responseJson.isJsonArray() ) {
            prescriptionResponse = responseJson.getAsJsonArray();

            if( prescriptionResponse != null && !prescriptionResponse.isJsonNull() && prescriptionResponse.size() != 0 ) {
                log.trace( "Process addressing for mipresNumber: {} ", numberMipres );
                processsPrescriptionAddressing( prescriptionResponse );
            }
            else {
                log.trace( "Not found addressing for mipresNumber: {} ", numberMipres );

            }
        }

    }

    private void processsPrescriptionAddressing( JsonArray addressingList ) {

        log.debug( "Enter to the method processsPrescriptionAddressing, for: {}.", addressingList );

        addressingList.forEach( addressing -> {
            processReceiveAddressing( addressing );

        } );
    }

    private void processReceiveAddressing( JsonElement jsonElement ) {

        JsonElement element;

        String mipresNumber, mipresId, noIdEps;

        //* Obtenemos el número de MIPRES
        element = JsonUtil.getElementFromPath( jsonElement, "NoPrescripcion" );
        mipresNumber = JsonUtil.getElementAsString( element );

        //* Obtenemos el mipresId
        element = JsonUtil.getElementFromPath( jsonElement, "ID" );
        mipresId = JsonUtil.getElementAsString( element );

        //* Obtenemos el nit eps
        element = JsonUtil.getElementFromPath( jsonElement, "NoIDEPS" );
        noIdEps = JsonUtil.getElementAsString( element );
       
        if( noIdEps.equals( EEps.NEPS.getNit() ) ) {
            processNeps(mipresNumber, mipresId, jsonElement);
        }else if( noIdEps.equals( EEps.FAMI.getNit() ) ) {
            processFami(mipresNumber, mipresId, jsonElement);
        }else if( noIdEps.equals( EEps.OI.getNit() ) ) {
            processOi(mipresNumber, mipresId, jsonElement);
        }

    }
    
    private void processNeps(final String mipresNumber, final String mipresId, final JsonElement jsonElement) {
    	Optional<TreatmentNeps> optTreatmentNeps;
    	optTreatmentNeps =
                this.treatmentNepsDao.findByMipressNumberAndMipresId( mipresNumber, mipresId );
            if( optTreatmentNeps.isPresent() ) {
                log.trace( "Init update treatment for numberMipres: {} and mipresId: {}", mipresNumber, mipresId );
                this.prescriptionHelper.updateTreatmentAddressing( jsonElement, optTreatmentNeps.get() );
            }
            else {
                log.trace( "Init create medicalPrescription and treatmentNeps for numberMipres: {} and mipresId: {}", mipresNumber, mipresId );
                this.prescriptionHelper.createMedicalPrescriptionAddressing( jsonElement, EEps.NEPS );
            }
    }
    
    private void processFami(final String mipresNumber, final String mipresId, final JsonElement jsonElement) {
    	Optional<TreatmentFami> optTreatmentFami;
    	 optTreatmentFami =
                 this.treatmentFamiDao.findByMipressNumberAndMipresId( mipresNumber, mipresId );
         if( optTreatmentFami.isPresent() ) {
             this.prescriptionHelperFami.updateTreatmentAddressing( jsonElement, optTreatmentFami.get() );
         }else {
             log.trace( "Init create medicalPrescription and treatmentFami for numberMipres: {} and mipresId: {}", mipresNumber, mipresId );
             this.prescriptionHelperFami.createMedicalPrescriptionAddressing( jsonElement, EEps.FAMI );
         }
    }
    
    private void processOi(final String mipresNumber, final String mipresId, final JsonElement jsonElement) {
    	Optional<TreatmentOI> optTreatmentOI;
    	optTreatmentOI =
                 this.treatmentOIDao.findByMipressNumberAndMipresId( mipresNumber, mipresId );
         if( optTreatmentOI.isPresent() ) {
             this.prescriptionHelperOI.updateTreatmentAddressing( jsonElement, optTreatmentOI.get() );
         }else {
             log.trace( "Init create medicalPrescription and treatmentFami for numberMipres: {} and mipresId: {}", mipresNumber, mipresId );
             this.prescriptionHelperOI.createMedicalPrescriptionAddressing( jsonElement, EEps.OI );
         }
    }

    private void getPrescriptionForLegacy( String numPreauthorization, String numberMipres )
        throws Exception {
        URI uri;

        log.trace( "Init getPrescriptionForLegacyNeps numPreauthorization: {}", numPreauthorization );
        uri = BasicHelper.buildUri( urlServiceNeps, numberMipres );

        consultService( uri, numPreauthorization );
    }

    private void consultService( URI uri, String numPreauthorization )
        throws Exception {
        ResponseEntity<?> response;

        String body;

        JsonArray prescriptionResponse;

        JsonElement prescriptionElement, responseJson;

        responseJson = null;

        log.trace( "consultLegacy get from the URI: {}. For EPS NEPS. Request: {}.", uri );

        Long initTime, endTime, totalTime;
        initTime = System.currentTimeMillis();
        log.info( "La peticion a {} inicio a las {}", uri, initTime );

        response = this.genericHelper.getService( uri, String.class );

        endTime = System.currentTimeMillis();
        log.info( "La peticion a {} finalizo a las {}", uri, endTime );
        totalTime = endTime - initTime;
        log.info( "El tiempo de respuesta de {} es de {} ms", uri, totalTime );

        body = ( String ) response.getBody();

        if( response.getStatusCode() == HttpStatus.OK && !isNull( body ) ) {
            responseJson = JsonParser.parseString( body );
        }

        if( responseJson != null ) {
            prescriptionElement = JsonUtil.getElementFromPath( responseJson, "obtenerPrescripcion" );

            if( prescriptionElement != null && prescriptionElement.isJsonArray() ) {
                prescriptionResponse = prescriptionElement.getAsJsonArray();

                if( prescriptionResponse != null && !prescriptionResponse.isJsonNull() ) {
                    log.trace( "Init to processsReceivePrescriptionNopbs for numPreauthorization: {} ", numPreauthorization );
                    processsReceivePrescriptionNopbs( prescriptionResponse );
                }
            }
        }
    }

    private void processsReceivePrescriptionNopbs( JsonArray prescriptionList ) throws Exception{

        log.debug( "Enter to the method processsReceivePrescriptionNopbs, for: {}.", prescriptionList );

        prescriptionList.forEach( prescription -> {
            List<JsonElement> elementList = this.prescriptionHelper.splitPrescriptionByTreatment( prescription );

            elementList.forEach( jsonElement -> {
                JsonElement element;

                String mipresNumber, noIdEps;

                //* Obtenemos el número de MIPRES
                element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
                mipresNumber = JsonUtil.getElementAsString( element );

                //* Obtenemos el nit eps
                element = JsonUtil.getElementFromPath( jsonElement, "NoIDEPS" );
                noIdEps = JsonUtil.getElementAsString( element );
                try {
                if( !StringUtils.isEmpty( mipresNumber ) ) {
                    if( noIdEps.equals( EEps.NEPS.getNit() ) ) {
                        log.trace( "init to create prescription recived nopbs for NEPS mipresNumber: {}", mipresNumber );
							processReceiveEpsNoPBSNeps( jsonElement );
						
                    }

                    if( noIdEps.equals( EEps.FAMI.getNit() ) ) {
                        log.trace( "init to create prescription recived nopbs for FAMI mipresNumber: {}", mipresNumber );
                        processReceiveEpsNoPBSFami( jsonElement );
                    }
                    
                    if( noIdEps.equals( EEps.OI.getNit() ) ) {
                        log.trace( "init to create prescription recived nopbs for OI mipresNumber: {}", mipresNumber );
                        processReceiveEpsNoPBSOI( jsonElement );
                    }
                }
                } catch (Exception e) {
                	 log.error("Ocurrio un erro publicando la formula RIPS", e.getStackTrace());
                     
				}
            } );
        } );
    }

    private void processReceiveEpsNoPBSNeps( JsonElement jsonElement ) throws Exception {

        JsonElement element;

        String numberPrescription;
        String preauthorization;
        String authorization;
        String materialId;
        String consecutiveTechnology;
        String deliveryNumber;
        String typeTechnology;
        String mipresNumber;

        element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
        mipresNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        numberPrescription = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].orden" );
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].numeroEntrega" );
        deliveryNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.tecnologia.tipo" );
        typeTechnology = JsonUtil.getElementAsString( element );

        Optional<TreatmentNeps> otreatment = this.treatmentNepsDao.findTreamentNepsEps( mipresNumber,
                                                                                        typeTechnology,
                                                                                        consecutiveTechnology,
                                                                                        deliveryNumber );
        TreatmentNeps treatment;

        if( otreatment.isPresent() ) {
            treatment = otreatment.get();

            if( StringUtils.hasLength( numberPrescription ) ) {
                treatment.setNumberPrescription( numberPrescription );
            }

            if( StringUtils.hasLength( authorization ) &&
                !authorization.equalsIgnoreCase( EValidationFormula.AUTHORIZATION_NULL.getName() ) ) {
                treatment.setAuthorizationNumber( authorization );
            }

            treatment.setPreauthorizationNumber( preauthorization );
            treatment.setMaterialId( materialId );
            treatment.setEpsMetadata( new Gson().toJson( jsonElement ) );

            treatment =   this.treatmentNepsDao.save( treatment );

            log.info( "Se encola la informacion de PROCESS_INFO_USER_NEPS {} ",
            		treatment.getIdTreatmentNeps() );

            this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_NEPS,
            		treatment.getIdTreatmentNeps());
        }
    }

    private void processReceiveEpsNoPBSFami( JsonElement jsonElement ) throws Exception {

        JsonElement element;

        String numberPrescription;
        String preauthorization;
        String authorization;
        String materialId;
        String consecutiveTechnology;
        String deliveryNumber;
        String typeTechnology;
        String mipresNumber;

        element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
        mipresNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        numberPrescription = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].orden" );
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].numeroEntrega" );
        deliveryNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.tecnologia.tipo" );
        typeTechnology = JsonUtil.getElementAsString( element );

        Optional<TreatmentFami> otreatment = this.treatmentFamiDao.findTreamentFamiEps( mipresNumber,
                                                                                        typeTechnology,
                                                                                        consecutiveTechnology,
                                                                                        deliveryNumber );
        TreatmentFami treatment;

        if( otreatment.isPresent() ) {
            treatment = otreatment.get();

            if( StringUtils.hasLength( numberPrescription ) ) {
                treatment.setNumberPrescription( numberPrescription );
            }

            if( StringUtils.hasLength( authorization ) &&
                !authorization.equalsIgnoreCase( EValidationFormula.AUTHORIZATION_NULL.getName() ) ) {
                treatment.setAuthorizationNumber( authorization );
            }

            treatment.setPreauthorizationNumber( preauthorization );
            treatment.setMaterialId( materialId );
            treatment.setEpsMetadata( new Gson().toJson( jsonElement ) );
            treatment =  this.treatmentFamiDao.save( treatment );
            log.info( "Se encola la informacion de PROCESS_INFO_USER_FAMI {} ",
   				 treatment.getIdTreatmentFami() );
   	       
            this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_FAMI,
   				treatment.getIdTreatmentFami());
   		
        }
    }
    
    
    private void processReceiveEpsNoPBSOI( JsonElement jsonElement ) throws Exception {

        JsonElement element;

        String numberPrescription;
        String preauthorization;
        String authorization;
        String materialId;
        String consecutiveTechnology;
        String deliveryNumber;
        String typeTechnology;
        String mipresNumber;

        element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
        mipresNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        numberPrescription = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
        preauthorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
        authorization = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
        materialId = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].orden" );
        consecutiveTechnology = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].numeroEntrega" );
        deliveryNumber = JsonUtil.getElementAsString( element );

        element = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.tecnologia.tipo" );
        typeTechnology = JsonUtil.getElementAsString( element );

        Optional<TreatmentOI> otreatment = this.treatmentOIDao.findTreamentOIEps( mipresNumber,
                                                                                        typeTechnology,
                                                                                        consecutiveTechnology,
                                                                                        deliveryNumber );
        TreatmentOI treatment;

        if( otreatment.isPresent() ) {
            treatment = otreatment.get();

            if( StringUtils.hasLength( numberPrescription ) ) {
                treatment.setNumberPrescription( numberPrescription );
            }

            if( StringUtils.hasLength( authorization ) &&
                !authorization.equalsIgnoreCase( EValidationFormula.AUTHORIZATION_NULL.getName() ) ) {
                treatment.setAuthorizationNumber( authorization );
            }

            treatment.setPreauthorizationNumber( preauthorization );
            treatment.setMaterialId( materialId );
            treatment.setEpsMetadata( new Gson().toJson( jsonElement ) );
            treatment =  this.treatmentOIDao.save( treatment );
            log.info( "Se encola la informacion de PROCESS_INFO_USER_FAMI {} ",
   				 treatment.getIdTreatment() );
   	       
            this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_FAMI,
   				treatment.getIdTreatment());
   		
        }
    }
}
