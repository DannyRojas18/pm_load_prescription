package com.colsubsidio.pm.load.prescription.listener;


import com.colsubsidio.pm.load.prescription.enums.QueveRabbitmqEnum;
import com.colsubsidio.pm.load.prescription.rabbit.PMReceive;
import com.colsubsidio.utilities.log.LogsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener para obtener las prescripciones notificadas por las eps en el
 * RabbitMQ de Colsubsidio
 *
 * @author Ingeneo
 */
@Component
public class DeliveryListener {

    @Autowired
    private LogsManager log;

    @Autowired
    private PMReceive pMReceive;

    private static Boolean isActive;

    public DeliveryListener() {
        DeliveryListener.isActive = false;
    }

    public void activeConsumer() {

        String queue;
        queue = QueveRabbitmqEnum.PRESCRIPCION_PBS.getName();

        try {
            if( !DeliveryListener.isActive ) {
                this.log.info( "Se ha activado el listener {}", queue );
                DeliveryListener.isActive = true;
                this.pMReceive.receives( QueveRabbitmqEnum.PRESCRIPCION_PBS );
            }
        }
        catch( Exception e ) {
            this.log.error( "Error al tratar de activar listener " + queue, e );
        }
    }

}
