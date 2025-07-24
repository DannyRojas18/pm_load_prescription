package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.config.RabbitConfig;
import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.helpers.LogicConsumerPBSCoom;
import com.colsubsidio.pm.load.prescription.helpers.LogicConsumerPBSFami;
import com.colsubsidio.pm.load.prescription.helpers.LogicConsumerPBSNeps;
import com.colsubsidio.pm.load.prescription.helpers.LogicConsumerPBSOI;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.rabbit.models.RabbitConnectionDto;
import com.colsubsidio.utilities.miscellaneous.rabbit.receive.ReceiveGeneric;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Listener para obtener las prescripciones notificadas por las eps NEPS en el RabbitMQ de Colsubsidio
 *
 * @author Ingeneo
 */
@Component
@RequiredArgsConstructor
public class DequeueListenerPBS {

    private final LogsManager log;
    private final ReceiveGeneric receive;
    private final RabbitConfig rabbitConfig;
    private final LogicConsumerPBSNeps logicConsumerConsultNeps;
    private final LogicConsumerPBSFami logicConsumerPBSFami;
    private final LogicConsumerPBSCoom logicConsumerPBSCoom;
    private final LogicConsumerPBSOI logicConsumerPBSOI;


    public void dequeueProcessNeps() {
        String queuePrescripcionPbs = EEps.NEPS.getQueuePrescripcionPbs();

        log.info( "Init find register to dequeue in dequeueProcessNeps by {}.", queuePrescripcionPbs );

        RabbitConnectionDto rabbitConectionDto;

        try {

            rabbitConectionDto = this.rabbitConfig.getConfigConection();

            this.receive.receive( queuePrescripcionPbs, rabbitConectionDto, this.logicConsumerConsultNeps );
        }
        catch( Exception e ) {
            log.error( "Error {}. In process dequeueProcessNeps by {}.", e, queuePrescripcionPbs );
        }
        finally {
            log.trace( "Finished the process of dequeueProcessNeps by: {}", queuePrescripcionPbs );
        }
    }

    public void dequeueProcessFami() {
        String queuePrescripcionPbs = EEps.FAMI.getQueuePrescripcionPbs();

        log.info( "Init find register to dequeue in dequeueProcessFami by {}.", queuePrescripcionPbs );

        RabbitConnectionDto rabbitConectionDto;

        try {

            rabbitConectionDto = this.rabbitConfig.getConfigConection();

            this.receive.receive( queuePrescripcionPbs, rabbitConectionDto, this.logicConsumerPBSFami );
        }
        catch( Exception e ) {
            log.error( "Error {}. In process dequeueProcessFami by {}.", e, queuePrescripcionPbs );
        }
        finally {
            log.trace( "Finished the process of dequeueProcessFami by: {}", queuePrescripcionPbs );
        }
    }

    public void dequeueProcessCoom() {
        String queuePrescripcionPbs = EEps.COOM.getQueuePrescripcionPbs();

        log.info( "Init find register to dequeue in dequeueProcessCoom by {}.", queuePrescripcionPbs );

        RabbitConnectionDto rabbitConectionDto;

        try {

            rabbitConectionDto = this.rabbitConfig.getConfigConection();

            this.receive.receive( queuePrescripcionPbs, rabbitConectionDto, this.logicConsumerPBSCoom );
        }
        catch( Exception e ) {
            log.error( "Error {}. In process dequeueProcessCoom by {}.", e, queuePrescripcionPbs );
        }
        finally {
            log.trace( "Finished the process of dequeueProcessCoom by: {}", queuePrescripcionPbs );
        }
    }
    
    public void dequeueProcessOI() {
        String queuePrescripcionPbs = EEps.OI.getQueuePrescripcionPbs();

        log.info( "Init find register to dequeue in dequeueProcessOI by {}.", queuePrescripcionPbs );

        RabbitConnectionDto rabbitConectionDto;

        try {

            rabbitConectionDto = this.rabbitConfig.getConfigConection();

            this.receive.receive( queuePrescripcionPbs, rabbitConectionDto, this.logicConsumerPBSOI );
        }
        catch( Exception e ) {
            log.error( "Error {}. In process dequeueProcessOI by {}.", e, queuePrescripcionPbs );
        }
        finally {
            log.trace( "Finished the process of dequeueProcessOI by: {}", queuePrescripcionPbs );
        }
    }

}
