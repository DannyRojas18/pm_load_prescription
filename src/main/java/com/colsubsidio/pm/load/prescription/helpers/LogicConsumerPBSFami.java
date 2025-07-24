/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.helpers;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EProcessState;
import com.colsubsidio.pm.load.prescription.models.dao.FieldValidatorDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionFamiDao;
import com.colsubsidio.pm.load.prescription.models.dao.ReceivedPrescriptionPbsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentFamiDao;
import com.colsubsidio.pm.load.prescription.models.dto.RequestReceiveEps;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionFami;
import com.colsubsidio.pm.load.prescription.models.entities.ReceivedPrescriptionPbs;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentFami;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.ICreatePbsFamiNotAuthorized;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.colsubsidio.utilities.miscellaneous.rabbit.receive.LogicConsumer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author Camilo Olivo
 */
@Component
public class LogicConsumerPBSFami extends LogicConsumer {

    private final LogsManager log;
    private final PrescriptionHelperFami prescriptionHelper;
    private final TreatmentFamiDao treatmentDao;
    private final ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao;
    private final FieldValidatorDao fieldValidatorDao;
    private final MedicalPrescriptionFamiDao medicalPrescriptionDao;
    private final ICreatePbsFamiNotAuthorized createPbsFamiNotAuthorized;
    final Gson gson = new Gson();

    @Value( "${colsubsidio.cum.separator}" )
    private String separatorCum;

    @Autowired
    public LogicConsumerPBSFami( LogsManager log, PrescriptionHelperFami prescriptionHelper,
                                 TreatmentFamiDao treatmentDao, ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao,
                                 FieldValidatorDao fieldValidatorDao,
                                 MedicalPrescriptionFamiDao medicalPrescriptionDao,
                                 ICreatePbsFamiNotAuthorized createPbsFamiNotAuthorized ) {
        this.log = log;
        this.prescriptionHelper = prescriptionHelper;
        this.treatmentDao = treatmentDao;
        this.receivedPrescriptionPbsDao = receivedPrescriptionPbsDao;
        this.fieldValidatorDao = fieldValidatorDao;
        this.medicalPrescriptionDao = medicalPrescriptionDao;
        this.createPbsFamiNotAuthorized = createPbsFamiNotAuthorized;
    }

    public void setProperties( String separatorCum ) {
        this.separatorCum = separatorCum;
    }

    @Override
    public void handleDelivery( String idReceivedPbs ) {
        ReceivedPrescriptionPbs receivedPrescriptionPbs;

        log.info( "Receive idReceivedPbs {} to start the prescription process fami", idReceivedPbs );

        Optional<ReceivedPrescriptionPbs> optionalReceivePres =
            this.receivedPrescriptionPbsDao.findByIdAndState( idReceivedPbs, EProcessState.UNPROCESSED.getId() );

        if( optionalReceivePres.isPresent() ) {
            receivedPrescriptionPbs = optionalReceivePres.get();

            log.trace( "Found the idReceivedPbs {} in receivedPrescriptionPbs with the information: {}", idReceivedPbs,
                       receivedPrescriptionPbs );

            processReceiveEps( receivedPrescriptionPbs );

            log.debug( "Finish idReceivedPbs: {} from queue PRESCRIPTION_PBS_FAMI.", idReceivedPbs );

        }
        else {
            log.trace( "Register not found for {} or processed", idReceivedPbs );
        }
    }

    @SuppressWarnings( "UseSpecificCatch" )
    private void processReceiveEps( ReceivedPrescriptionPbs receivedPrescriptionPbs ) {
        JsonArray prescriptionList;

        String idReceivedPbs, metadata;

        idReceivedPbs = receivedPrescriptionPbs.getId();
        metadata = receivedPrescriptionPbs.getMetadata();

        ObjectMapper objectMapper;
        objectMapper = new ObjectMapper();
        objectMapper.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );

        try {

            if( !receivedPrescriptionPbs.isAuthorized() ) {

                RequestReceiveEps[] requestReceiveEpsArray;
                requestReceiveEpsArray = objectMapper.readValue( metadata, RequestReceiveEps[].class );
                List<RequestReceiveEps> requestReceiveEpsList = Arrays.asList( requestReceiveEpsArray );
                this.createPbsFamiNotAuthorized.receivePrescription( EEps.FAMI, requestReceiveEpsList );

            }
            else {

                prescriptionList = JsonParser.parseString( metadata ).getAsJsonArray();

                processsReceiveEps( prescriptionList, idReceivedPbs );

            }

            receivedPrescriptionPbs.setState( EProcessState.PROCESSED.getId() );

            log.trace( "Finish ok the process. idReceivedPbs: {}. Metadata: {}", idReceivedPbs, metadata );
        }
        catch( Exception exe ) {
            receivedPrescriptionPbs.setState( EProcessState.FAILED.getId() );

            log.error( "Error in process. Error {}. idReceivedPbs: {}. Metadata: {}", exe, idReceivedPbs, metadata );
        }
        finally {
            this.receivedPrescriptionPbsDao.save( receivedPrescriptionPbs );
        }
    }

    private void processsReceiveEps( JsonArray prescriptionList, String idReceivedPbs ) throws Exception {

        int countError = 0;
        log.debug( "Enter to the method processsReceiveEps. idReceivedPbs {}., for: {}.", idReceivedPbs, prescriptionList );

        for( JsonElement prescription : prescriptionList ) {
            List<JsonElement> elementList = this.prescriptionHelper.splitPrescriptionByTreatment( prescription );
            for( JsonElement jsonElement : elementList ) {

                JsonElement element;

                String mipresNumber;

                //* Obtenemos el número de MIPRES
                element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
                mipresNumber = JsonUtil.getElementAsString( element );

                //* Validamos si es NoPBS o PBS.
                if( !StringUtils.hasLength( mipresNumber ) || Strings.isNullOrEmpty( mipresNumber ) || mipresNumber.toLowerCase().equals( "null" ) ) {
                    processReceiveEpsPBS( jsonElement, idReceivedPbs );
                }
                else {
                    countError++;
                    if( prescriptionList.size() == countError ) {
                        throw new EmptyStackException();
                    }
                }
            }
        }
    }

    private void processReceiveEpsPBS( JsonElement jsonElement, String idReceivedPbs ) throws Exception {
        Optional<TreatmentFami> optTreatment;

        Optional<MedicalPrescriptionFami> optMedicalPrescription;

        TreatmentFami treatment;

        JsonElement element, elementCum, elementMapis;

        String numberPrescription, cum, mapis;

        //* Obtenemos el numero de prescripcion.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        numberPrescription = JsonUtil.getElementAsString( element );

        if( !Strings.isNullOrEmpty( numberPrescription ) ) {

            log.debug( "Init the process processReceiveEpsPBS. IdReceivedPbs {}. numberPrescription: {}.",
                       idReceivedPbs, numberPrescription );

            //* Obtenemos el cum o el mapis del tratamiento.
            elementCum = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
            cum = JsonUtil.getElementAsString( elementCum );

            elementMapis = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
            mapis = JsonUtil.getElementAsString( elementMapis );

            if( !Strings.isNullOrEmpty( cum ) && !cum.equalsIgnoreCase( "null" ) && !cum.equals( "" ) ) {
                optTreatment = this.treatmentDao.findByNumberPrescriptionAndCum( numberPrescription, cum );
            }
            else {
                optTreatment = this.treatmentDao.findByNumberPrescriptionAndMaterialId( numberPrescription, mapis );
            }

            if( optTreatment.isPresent() ) {
                treatment = optTreatment.get();

                log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. updateTreatmentFromMetadata: {}.",
                           idReceivedPbs, treatment );

                this.prescriptionHelper.updateTreatmentFromMetadata( jsonElement, treatment );
            }
            else {
                optMedicalPrescription =
                    this.medicalPrescriptionDao.findByNumberPrescription( numberPrescription );

                if( optMedicalPrescription.isPresent() ) {
                    log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. createTreatment: {}.",
                               idReceivedPbs, optMedicalPrescription.get() );

                    this.prescriptionHelper.createTreatment( jsonElement, optMedicalPrescription.get() );
                }
                else {
                    log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. createMedicalPrescription: {}.",
                               idReceivedPbs, jsonElement );
                    this.prescriptionHelper.createMedicalPrescription( jsonElement, EEps.FAMI );
                }
            }
        }
        else {
            log.error( "Does not contain formula number. IdReceivedPbs {}.", idReceivedPbs );
            throw new EmptyStackException();
        }

    }

    @Override
    @SuppressWarnings( "CloneDoesntCallSuperClone" )
    public Object clone()
        throws CloneNotSupportedException {
        LogicConsumerPBSFami logicConsumer;

        logicConsumer = new LogicConsumerPBSFami( this.log, this.prescriptionHelper, this.treatmentDao,
                                                  this.receivedPrescriptionPbsDao, this.fieldValidatorDao,
                                                  this.medicalPrescriptionDao,
                                                  this.createPbsFamiNotAuthorized );

        logicConsumer.setProperties( this.separatorCum );

        return logicConsumer;
    }
}
