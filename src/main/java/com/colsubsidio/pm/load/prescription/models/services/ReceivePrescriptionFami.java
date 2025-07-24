/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionSource;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.pm.load.prescription.enums.ETypePrescription;
import com.colsubsidio.pm.load.prescription.enums.PrescriptionState;
import com.colsubsidio.pm.load.prescription.helpers.PMPublishExchange;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelper;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionFamiDao;
import com.colsubsidio.pm.load.prescription.models.dao.ReceivedPrescriptionPbsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentFamiDao;
import com.colsubsidio.pm.load.prescription.models.dto.FormulaPbs;
import com.colsubsidio.pm.load.prescription.models.dto.FormulaState;
import com.colsubsidio.pm.load.prescription.models.dto.Paciente;
import com.colsubsidio.pm.load.prescription.models.dto.RequestReceiveEps;
import com.colsubsidio.pm.load.prescription.models.dto.ResponseStatus;
import com.colsubsidio.pm.load.prescription.models.dto.ResultReceiveEps;
import com.colsubsidio.pm.load.prescription.models.dto.TreatmentData;
import com.colsubsidio.pm.load.prescription.utilities.Constants;
import com.colsubsidio.pm.load.prescription.utilities.DateParseUtil;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentFami;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionFami;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.ICreatePbsFamiNotAuthorized;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Camilo
 */
@Service
public class ReceivePrescriptionFami implements ICreatePbsFamiNotAuthorized {

    private final LogsManager log;
    private final MedicalPrescriptionFamiDao medicalPrescriptionDao;
    private final TreatmentFamiDao treatmentFamiDao;
    private final PrescriptionHelper prescriptionHelper;

    private final PMPublishExchange pMPublishExchange;

    @Autowired
    public ReceivePrescriptionFami( LogsManager log,
                                    MedicalPrescriptionFamiDao medicalPrescriptionDao,
                                    TreatmentFamiDao treatmentFamiDao,
                                    PrescriptionHelper prescriptionHelper,
                                    ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao,
                                    PMPublishExchange pMPublishExchange) {
        this.log = log;
        this.medicalPrescriptionDao = medicalPrescriptionDao;
        this.treatmentFamiDao = treatmentFamiDao;
        this.prescriptionHelper = prescriptionHelper;
        this.pMPublishExchange= pMPublishExchange;
    }

    @Override
    public void receivePrescription( EEps eps, List<RequestReceiveEps> requestReceiveEpsList ) {
        log.debug( "Init receive Eps: {}. Request size: {}. request: {} ", eps, requestReceiveEpsList.size(),
                   requestReceiveEpsList );

        List<ResultReceiveEps> resultReceiveEps = new ArrayList<>();

        String metadata = JsonUtil.objectToJson( requestReceiveEpsList );
        log.debug( "Request receive: {} ", metadata );

        try {

            for( RequestReceiveEps requestReceiveEps : requestReceiveEpsList ) {

                FormulaPbs formula;

                String numberPrescription;

                List<TreatmentData> treatments;

                double duration;

                treatments = requestReceiveEps.getFormula().getTreatmentData();

                List<TreatmentData> allTreatments = requestReceiveEps.getFormula().getTreatmentData()
                    .stream().map( TreatmentData::new )
                    .collect( Collectors.toList() );

                TreatmentData maxTreatment = treatments.stream().max(
                    java.util.Comparator.comparing( TreatmentData::getCantEntrega ) )
                    .orElseThrow( java.util.NoSuchElementException::new );

                int maxCant = maxTreatment.getCantEntrega();

                formula = requestReceiveEps.getFormula();
                numberPrescription = formula.getNumero();

                String dateRequest = DateParseUtil.parseDate( formula.getFechaSolicitud() );
                formula.setFechaSolicitud( dateRequest );

                log.debug( "Date request: {}. Eps: {}. assigning max delivery: {}. number prescription: {} ",
                           dateRequest, eps, maxCant, numberPrescription );

                duration = Double.parseDouble( formula.getVigencia() );

                assignMaxDelivery( numberPrescription, eps, maxCant, formula, duration, allTreatments, resultReceiveEps,
                                   requestReceiveEps );
            }

        }
        catch( Exception e ) {
            log.error( "Error {}. Eps: {}. Metadata {}", e, eps, metadata );
        }

    }

    /**
     * Asigna el max delivery
     *
     * @param numberPrescription numero de la prescripcion
     * @param eps nombre de la eps
     * @param maxDelivery numero maximo de entraga
     * @param formula prescripcion
     * @param duration duracion de la vigencia
     * @param treatmentData cantidad de tratamientos
     * @param resultList lista de errores
     * @param item item del request
     */
    private void assignMaxDelivery( String numberPrescription, EEps eps, Integer maxDelivery, FormulaPbs formula,
                                    Double duration, List<TreatmentData> treatmentData,
                                    List<ResultReceiveEps> resultList, RequestReceiveEps item ) {

        Date serviceDate = null;
        Paciente patient;

        patient = item.getPaciente();

        for( int i = 0; i < maxDelivery; i++ ) {

            RequestReceiveEps newRequest;
            List<TreatmentData> listTreatment;

            if( i == 0 ) {
                listTreatment = treatmentData.stream().map( TreatmentData::new ).collect( Collectors.toList() );
                serviceDate = DateParseUtil.parseStringDate( formula.getFechaSolicitud() );
                log.info( "number prescription: {}. set service date: {}. of request date: {} ", serviceDate,
                          formula.getFechaSolicitud(), numberPrescription );
            }
            else {
                int cantDelivery = i;
                listTreatment = treatmentData.stream().map( TreatmentData::new )
                    .collect( Collectors.toList() ).stream()
                    .filter( tr -> tr.getCantEntrega() > cantDelivery )
                    .collect( Collectors.toList() );

                int DURATION_VALUE = 1;
                int sumDuration = duration.intValue() + DURATION_VALUE;
                log.info( "sum duration: {}. Eps: {}. numberPrescription: {}", sumDuration, eps,
                          numberPrescription );
                serviceDate = DateParseUtil.sumRestAddDay( serviceDate, sumDuration );
                String number = numberPrescription + Constants.DASH + ( i + 1 );
                formula.setNumero( number );
            }

            String authorizationNumber = formula.getAutorizacion();
            log.info( "validate number authorization is null: {}. Eps: {}. number prescription: {}",
                      authorizationNumber, eps, formula.getNumero() );

            if( Strings.isNullOrEmpty( authorizationNumber ) ) {
                authorizationNumber = formula.getNumero();
            }

            formula.setFechaServicio( DateParseUtil.parseDateString( serviceDate ) );
            formula.setFechaVencimiento( DateParseUtil.parseDateString(
                DateParseUtil.sumRestAddDay( serviceDate, duration.intValue() ) ) );

            log.info( "Processing treatments list: {}. Eps: {}. Number Prescription: {}", listTreatment.size(),
                      eps, numberPrescription );

            if( !listTreatment.isEmpty() ) {
                List<TreatmentData> listTreat = new ArrayList<>();
                for( TreatmentData data : listTreatment ) {

                    Double quantity;
                    if( !Strings.isNullOrEmpty( data.getCantidad() ) ) {
                        quantity = Double.parseDouble( data.getCantidad() );
                    }
                    else {
                        quantity = Double.MIN_VALUE;
                    }

                    data.setCantidad( String.valueOf( quantity.intValue() ) );
                    data.setNumeroEntrega( String.valueOf( i + 1 ) );
                    data.setFechaVencimiento( formula.getFechaVencimiento() );
                    data.setEstado( FormulaState.NO_ENTREGADO.getName() );
                    listTreat.add( data );
                }
                formula.setTreatmentData( null );
                formula.setTreatmentData( listTreat );
                log.info( "Update formula: {} Eps: {}. Number Prescription: {}", formula, eps,
                          numberPrescription );
            }

            item.setFormula( null );
            item.setFormula( formula );

            newRequest = new RequestReceiveEps();
            newRequest.setFormula( formula );
            newRequest.setPaciente( patient );

            String metadata = JsonUtil.objectToJson( newRequest );

            log.info( "Metadata: {}. Eps: {}. Number Prescription: {}", metadata, eps,
                      numberPrescription );

            validateNumberPrescription( eps, formula.getNumero(), authorizationNumber, metadata, resultList, newRequest );

            if( !resultList.isEmpty() ) {
                return;
            }
        }

    }

    /**
     * Valida la si existe la prescription en la tabla medical prescription.
     *
     * @param eps Eps
     * @param numberPrescription numero de la prescription
     * @param authorizationNumber numero de la autorizacion
     * @param metadata
     * @param resultList
     * @param newRequest
     */
    public void validateNumberPrescription( EEps eps, String numberPrescription, String authorizationNumber,
                                            String metadata, List<ResultReceiveEps> resultList,
                                            RequestReceiveEps newRequest ) {
        List<TreatmentFami> treatment;

        log.info( "Validate numberPrescription: {}. Eps: {}. authorizationNumber: {} ", numberPrescription,
                  eps, authorizationNumber );

        treatment = treatmentFamiDao.findByNumberPrescription( numberPrescription );

        if( !treatment.isEmpty() ) {

            updateTreatments( treatment.get( 0 ).getIdMedicalPrescriptionFami(), authorizationNumber, eps, numberPrescription, newRequest, resultList,
                              metadata );
        }
        else {
            saveNoExists( eps, numberPrescription, authorizationNumber, newRequest, resultList, metadata );
        }
    }

    /**
     * Metodo para crear o actualizar un treatmens
     *
     * @param treatmenList Lista con los treatments a crear o actualizar
     * @param authorizationNumber numbero de la autorizacion
     * @param eps eps
     * @param numberPrescription numbero de la prescripcion
     * @param newRequest json con la peticion
     * @param resultList lista de errores
     * @param metadata metada del json
     */
    private void updateTreatments( String IdMedicalPrescriptionFami, String authorizationNumber,
                                   EEps eps, String numberPrescription, RequestReceiveEps newRequest,
                                   List<ResultReceiveEps> resultList, String metadata ) {

        log.info( "Update treatments : {}. Eps: {}. NumberPrescription: {}. request: {} ", eps, numberPrescription,
                  newRequest );

        FormulaPbs formula = newRequest.getFormula();

        try {

            JsonElement jsonElement;

            String epsMetadata = null;
            jsonElement = JsonParser.parseString( metadata );
            List<JsonElement> treatmentJsonElements =
                prescriptionHelper.splitPrescriptionByTreatment( jsonElement );

            for( JsonElement element : treatmentJsonElements ) {
                JsonElement mapisJsonElement =
                    JsonUtil.getElementFromPath( element, "formula.tratamiento[0].producto.mapis" );

                String mapis;
                mapis = JsonUtil.getElementAsString( mapisJsonElement );

                if( !Strings.isNullOrEmpty( mapis ) ) {

                    Optional<TreatmentFami> treatmentFamiOp = treatmentFamiDao.findByNumberPrescription( numberPrescription, mapis );

                    if( treatmentFamiOp.isPresent() ) {

                        TreatmentFami treatmentFami = treatmentFamiOp.get();

                        if( Strings.isNullOrEmpty( treatmentFami.getEpsMetadata() ) ) {

                            epsMetadata = JsonUtil.objectToJson( element );
                            treatmentFami.setEpsMetadata( epsMetadata );
                            treatmentFami=  treatmentFamiDao.save( treatmentFami );
                            //ENCOLAR PRESCRIPCION FAMI
                            log.info( "Se encola la informacion de fami PROCESS_INFO_USER_NEPS {} ",
                            		treatmentFami.getIdTreatmentFami() );
                            this.pMPublishExchange.publishExchangeValidation( EQueueRabbitmq.PROCESS_INFO_USER_FAMI,  treatmentFami.getIdTreatmentFami() );
                          
                           
                        }
                    }
                    else {
                        saveTreadment( element, IdMedicalPrescriptionFami, numberPrescription, authorizationNumber, formula, mapis );
                    }

                }
            }

        }
        catch( Exception ex ) {
            log.error(
                "Error createOrUpdateTreatmentsWithAuthorization: {}. Eps: {}. NumberPrescription: {}. " +
                "Request: {}", ex, eps, numberPrescription, newRequest );
            resultList.addAll( messageError( "Error al actualizar la medication. " + numberPrescription, true ) );
        }
        finally {
            log.info( "Finish update treatments {}. Eps: {}. NumberPrescription: {}. request: {} ", eps,
                      numberPrescription, newRequest );
        }
    }

    private void saveNoExists( EEps eps, String numberPrescription, String authorizationNumber,
                               RequestReceiveEps newRequest, List<ResultReceiveEps> resultList, String metadata ) {

        log.info( "Init create medical prescription: {}. Eps: {}. authorizationNumber: {}. Request: {} ",
                  numberPrescription, eps, authorizationNumber, newRequest );

        String idMedicalPrescription = saveMedicalPrescription( eps, newRequest, numberPrescription );

        if( Strings.isNullOrEmpty( idMedicalPrescription ) ) {
            log.error( "Error create medical prescription: {}. Eps: {}. authorizationNumber: {}. " +
                       "Request: {} ", numberPrescription, eps, authorizationNumber, newRequest );
            resultList.addAll( messageError( "Error al actualizar la medication. " + numberPrescription, true ) );
        }

        saveTreatment( eps, idMedicalPrescription, newRequest, numberPrescription, authorizationNumber, metadata );
    }

    /**
     * Crea un nuevo registro en la medical prescripcion de fami
     *
     * @param eps nombre de la eps
     * @param newRequest Request del json
     * @param numberPrescription numero de la prescripcion
     * @return exitoso o no
     */
    private String saveMedicalPrescription( EEps eps, RequestReceiveEps newRequest, String numberPrescription ) {

        String idMedicalPrescription = null;
        Paciente patien;
        patien = newRequest.getPaciente();

        try {
            MedicalPrescriptionFami medicalPrescription = MedicalPrescriptionFami
                .builder()
                .idPrescriptionSource( EPrescriptionSource.FAMI.getId() )
                .idEps( eps.getIdEps() )
                .mipresNumber( null )
                .epsNit( eps.getNit() )
                .epsCode( null )
                .prescriptionType( ETypePrescription.PBS.getId() )
                .numberIdPatient( patien.getDocumento().getNumero() )
                .typeIdPatient( patien.getDocumento().getTipo() )
                .build();
            this.medicalPrescriptionDao.save( medicalPrescription );
            idMedicalPrescription = medicalPrescription.getIdMedicalPrescriptionFami();
        }
        catch( Exception ex ) {
            log.error( "Error create medical prescription error {}. Request: {}. Eps: {}. NumberPrescription {} ", ex, newRequest, eps,
                       numberPrescription );
        }
        finally {
            log.info( "Finish create medical  prescription Request: {}. Eps: {}. ", newRequest, eps,
                      numberPrescription );
        }
        return idMedicalPrescription;
    }

    /**
     * Crea una nueva formula
     *
     * @param eps Nombre de la eps
     * @param idMedicalPrescription id medical prescription
     * @param newRequest request que contiene la formula
     * @param numberPrescription numero de la prescription
     * @param authorizationNumber numero de la autorizacion
     * @param metadata metadata
     * @return exitoso o no
     */
    private boolean saveTreatment( EEps eps, String idMedicalPrescription, RequestReceiveEps newRequest,
                                   String numberPrescription, String authorizationNumber, String metadata ) {

        boolean result;
        FormulaPbs formula;
        JsonElement jsonElement;

        result = Boolean.TRUE;

        log.info( "Creating treatment with IdMedicalPrescription: {}. EPS: {}. Request: {}. Number Prescription {}",
                  idMedicalPrescription, eps, newRequest, numberPrescription );

        try {

            if( !Strings.isNullOrEmpty( authorizationNumber ) &&
                !numberPrescription.equalsIgnoreCase( authorizationNumber ) ) {
                numberPrescription = authorizationNumber;
            }

            formula = newRequest.getFormula();
            jsonElement = JsonParser.parseString( metadata );
            List<JsonElement> treatmentList = this.prescriptionHelper.splitPrescriptionByTreatment( jsonElement );
            for( JsonElement element : treatmentList ) {

                JsonElement mapisElement;
                String mapis;

                mapisElement =
                    JsonUtil.getElementFromPath( element, "formula.tratamiento[0].producto.mapis" );

                mapis = JsonUtil.getElementAsString( mapisElement );

                if( !treatmentFamiDao.findByNumberPrescription( numberPrescription, mapis ).isPresent() ) {

                    saveTreadment( element, idMedicalPrescription, numberPrescription, authorizationNumber, formula, mapis );

                }
                else {
                    log.info( "The treatment could not be created because it already exists numberPrescription {} ", numberPrescription );
                }
            }

        }
        catch( Exception e ) {
            result = Boolean.FALSE;
            log.error( "Error saved treatment numberPrescription: {}. Eps: {}. IdMedicalPrescription: {}", e,
                       numberPrescription, eps, idMedicalPrescription );
        }

        return result;
    }

    /**
     * Registra los mensajes
     *
     * @param message parametro que contiene el error
     * @param error mensaje de error
     * @return lista de mensajes de error o de exito
     */
    private List<ResultReceiveEps> messageError( String message, boolean error ) {

        ResultReceiveEps result = new ResultReceiveEps();

        if( error ) {
            result.setCodigo( ResponseStatus.INTERNAL_ERROR );
        }
        else {
            result.setCodigo( ResponseStatus.SUCCESS );
        }

        result.setDescripcion( message );

        List<ResultReceiveEps> list = new ArrayList<>();
        list.add( result );

        return list;
    }

    private void saveTreadment( JsonElement element,
                                String idMedicalPrescription,
                                String numberPrescription,
                                String authorizationNumber,
                                FormulaPbs formula,
                                String mapis ) throws Exception {
        JsonElement deliveryNumberElement;
        String deliveryNumber;

        deliveryNumberElement =
            JsonUtil.getElementFromPath( element, "formula.tratamiento[0].numeroEntrega" );

        deliveryNumber = JsonUtil.getElementAsString( deliveryNumberElement );
        TreatmentFami treatment = TreatmentFami.builder()
            .idMedicalPrescriptionFami( idMedicalPrescription )
            .numberPrescription( numberPrescription )
            .idFormulaState( FormulaState.NO_ENTREGADO.getIdFormulaState() )
            .preauthorizationNumber( authorizationNumber )
            .idPrescriptionStatus( PrescriptionState.CONSULTADA.getId() )
            .maxDeliveryDate( DateParseUtil.parseStringDate(
                formula.getFechaVencimiento() ) )
            .materialId( mapis )
            .deliveryNumber( deliveryNumber )
            .epsMetadata( new Gson().toJson( element ) )
            .attempts( 0 )
            .build();
      
        treatment= treatmentFamiDao.save( treatment );
        //ENCOLAR PRESCRIPCION FAMI
        log.info( "Se encola la informacion de fami PROCESS_INFO_USER_FAMI {} ",
        		treatment.getIdTreatmentFami() );
        this.pMPublishExchange.publishExchangeValidation( EQueueRabbitmq.PROCESS_INFO_USER_FAMI,  treatment.getIdTreatmentFami() );
        log.info( "Save IdMedicalPrescription: {}. EPS: {}. Request: {}. Number Prescription {}. ",
                  idMedicalPrescription, EEps.FAMI, element.toString(), numberPrescription );
    }

}
