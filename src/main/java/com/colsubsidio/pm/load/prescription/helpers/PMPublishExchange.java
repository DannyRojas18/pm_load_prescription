/*
 * Codigo fuente propiedad de Colsubsidio Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.helpers;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.colsubsidio.pm.load.prescription.config.RabbitConfig;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.rabbit.models.RabbitConnectionExchangeDto;
import com.colsubsidio.utilities.miscellaneous.rabbit.publish.PublishExchange;

@Component
public class PMPublishExchange {
    private final LogsManager log;

    private final PublishExchange publishExchange;

    private final RabbitConfig config;

    @Autowired
    public PMPublishExchange( LogsManager log, PublishExchange pmPublishExchange,
                              RabbitConfig config ) {
        this.publishExchange = pmPublishExchange;
        this.config = config;
        this.log = log;
    }
  
    public void publishExchangeValidation( EQueueRabbitmq prescriptionState, String message )
            throws Exception {
            String nameExchange;

            RabbitConnectionExchangeDto rabbitConnectionDto;

            nameExchange = prescriptionState.name();

            try {
                rabbitConnectionDto = this.config.getConfigConectionExchange( nameExchange );

                log.trace( "Enviando {} al exchange {} ", message, rabbitConnectionDto.getExchange() );

                this.publishExchange.publishExchange( message, rabbitConnectionDto );

            }
            catch( IOException | KeyManagementException | NoSuchAlgorithmException | TimeoutException e ) {
                log.error( "Error al tratar de publicar el estado {}.", e, nameExchange );
            }
        }
}
