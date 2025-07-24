package com.colsubsidio.pm.load.prescription.models.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionSource;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.pm.load.prescription.enums.ETypePrescription;
import com.colsubsidio.pm.load.prescription.enums.PrescriptionState;
import com.colsubsidio.pm.load.prescription.helpers.PMPublishExchange;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelper;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionOIDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentOIDao;
import com.colsubsidio.pm.load.prescription.models.dto.FormulaPbs;
import com.colsubsidio.pm.load.prescription.models.dto.FormulaState;
import com.colsubsidio.pm.load.prescription.models.dto.Paciente;
import com.colsubsidio.pm.load.prescription.models.dto.RequestReceiveEps;
import com.colsubsidio.pm.load.prescription.models.dto.ResponseStatus;
import com.colsubsidio.pm.load.prescription.models.dto.ResultReceiveEps;
import com.colsubsidio.pm.load.prescription.models.dto.TreatmentData;
import com.colsubsidio.pm.load.prescription.models.entities.Eps;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionOI;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentOI;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.ICreatePbsOINotAuthorized;
import com.colsubsidio.pm.load.prescription.utilities.Constants;
import com.colsubsidio.pm.load.prescription.utilities.DateParseUtil;
import com.colsubsidio.pm.load.prescription.utilities.Utils;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author dchavarro
 * @version 1.0
 * Clase encargada de resivir las prescripciones de otros canales
 *
 */
@Service
@RequiredArgsConstructor
public class ReceivePrescriptionOI implements ICreatePbsOINotAuthorized {
	
	private final LogsManager log;
    private final MedicalPrescriptionOIDao medicalPrescriptionDao;
    private final TreatmentOIDao treatmentOIDao;
    private final PrescriptionHelper prescriptionHelper;

    private final PMPublishExchange pMPublishExchange;
    private final Utils utils;

    /**
     * Metodo encargado de resivir la prescripciones de otros canales
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param eps Enum de las eps
     * @param List<RequestReceiveEps>
     */
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
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
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
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
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
        List<TreatmentOI> treatment;

        log.info( "Validate numberPrescription: {}. Eps: {}. authorizationNumber: {} ", numberPrescription,
                  eps, authorizationNumber );

        treatment = treatmentOIDao.findByNumberPrescription( numberPrescription );

        if( !treatment.isEmpty() ) {

            updateTreatments( treatment.get( 0 ).getIdMedicalPrescription(), authorizationNumber, eps, numberPrescription, newRequest, resultList,
                              metadata );
        }
        else {
            saveNoExists( eps, numberPrescription, authorizationNumber, newRequest, resultList, metadata );
        }
    }

    /**
     * Metodo para crear o actualizar un treatmens
     *
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param treatmenList Lista con los treatments a crear o actualizar
     * @param authorizationNumber numbero de la autorizacion
     * @param eps eps
     * @param numberPrescription numbero de la prescripcion
     * @param newRequest json con la peticion
     * @param resultList lista de errores
     * @param metadata metada del json
     */
    private void updateTreatments( String IdMedicalPrescription, String authorizationNumber,
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

                    Optional<TreatmentOI> treatmentOIOp = treatmentOIDao.findByNumberPrescription( numberPrescription, mapis );

                    if( treatmentOIOp.isPresent() ) {

                        TreatmentOI treatmentOI = treatmentOIOp.get();

                        if( Strings.isNullOrEmpty( treatmentOI.getEpsMetadata() ) ) {

                            epsMetadata = JsonUtil.objectToJson( element );
                            treatmentOI.setEpsMetadata( epsMetadata );
                            treatmentOI=  treatmentOIDao.save( treatmentOI );
                            //ENCOLAR PRESCRIPCION oi
                            log.info( "Se encola la informacion de oi PROCESS_INFO_USER_OI {} ",
                            		treatmentOI.getIdTreatment() );
                            this.pMPublishExchange.publishExchangeValidation( EQueueRabbitmq.PROCESS_INFO_USER_OI,  treatmentOI.getIdTreatment() );
                          
                           
                        }
                    }
                    else {
                        saveTreadment( element, IdMedicalPrescription, numberPrescription, authorizationNumber, formula, mapis );
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

    /**
     * Metodo encargado de guardar si no existe el medical prescripcion
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param eps
     * @param numberPrescription
     * @param authorizationNumber
     * @param newRequest
     * @param resultList
     * @param metadata
     */
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
     * Crea un nuevo registro en la medical prescripcion de oi
     *
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param eps nombre de la eps
     * @param newRequest Request del json
     * @param numberPrescription numero de la prescripcion
     * @return exitoso o no
     */
    private String saveMedicalPrescription( EEps eps, RequestReceiveEps newRequest, String numberPrescription ) {

        String idMedicalPrescription = null;
        Paciente patien;
        patien = newRequest.getPaciente();
        String codeInsurance =getIdSeat(newRequest);
        Eps entity = this.utils.getEpsToInsurance(codeInsurance);
        try {
            MedicalPrescriptionOI medicalPrescription = MedicalPrescriptionOI
                .builder()
                .idPrescriptionSource( EPrescriptionSource.OI.getId() )
                .idEps(Objects.nonNull(entity) ? entity.getId(): eps.getIdEps() )
                .mipresNumber( null )
                .epsNit(Objects.nonNull(entity) ? entity.getNit(): eps.getNit() )
                .epsCode( Objects.nonNull(entity) ? entity.getCodigo():null )
                .prescriptionType( ETypePrescription.PBS.getId() )
                .numberIdPatient( patien.getDocumento().getNumero() )
                .typeIdPatient( patien.getDocumento().getTipo() )
                .idSeat(Integer.parseInt(codeInsurance))
                .build();
            this.medicalPrescriptionDao.save( medicalPrescription );
            idMedicalPrescription = medicalPrescription.getIdMedicalPrescription();
        }
        catch( Exception ex ) {
            log.error( "Error create medical prescription error {}. Request: {}. Eps: {}. NumberPrescription {} ", ex, newRequest, entity,
                       numberPrescription );
        }
        finally {
            log.info( "Finish create medical  prescription Request: {}. Eps: {}. ", newRequest, entity,
                      numberPrescription );
        }
        return idMedicalPrescription;
    }
    
    /**
     * Metodo encargado de obtener el codigo de la aseguradora
     *
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param newRequest
     * @return Devuelve el codigo de la aseguradora
     */
    private String getIdSeat(final RequestReceiveEps newRequest) {
    	log.info("Inicio metodo getIdSeat: {} ");
    	String idSeat =null;
    	
    	idSeat = Objects.nonNull(newRequest.getFormula()) && Objects.nonNull(newRequest.getFormula().getDispensacion()) && Objects.nonNull(newRequest.getFormula().getDispensacion().getAseguradoraFormula()) && Objects.nonNull(newRequest.getFormula().getDispensacion().getAseguradoraFormula().getCodigo()) ? newRequest.getFormula().getDispensacion().getAseguradoraFormula().getCodigo() :null;
    	log.info("Fin metodo getIdSeat: {} ", idSeat);
    	return idSeat;
    }

    /**
     * Crea una nueva formula
     *
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
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

                if( !treatmentOIDao.findByNumberPrescription( numberPrescription, mapis ).isPresent() ) {

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
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
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

    /**
     * Metodo encargado de crear el tratamiento
     * 
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param element
     * @param idMedicalPrescription
     * @param numberPrescription
     * @param authorizationNumber
     * @param formula
     * @param mapis
     * @throws Exception
     */
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
        TreatmentOI treatment = TreatmentOI.builder()
            .idMedicalPrescription( idMedicalPrescription )
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
      
        treatment= treatmentOIDao.save( treatment );
        //ENCOLAR PRESCRIPCION OI
        log.info( "Se encola la informacion de oi PROCESS_INFO_USER_OI {} ",
        		treatment.getIdTreatment() );
        this.pMPublishExchange.publishExchangeValidation( EQueueRabbitmq.PROCESS_INFO_USER_OI,  treatment.getIdTreatment() );
        log.info( "Save IdMedicalPrescription: {}. EPS: {}. Request: {}. Number Prescription {}. ",
                  idMedicalPrescription, EEps.OI, element.toString(), numberPrescription );
    }
}
