package com.colsubsidio.pm.load.prescription.rabbit;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.colsubsidio.pm.load.prescription.models.services.interfaces.ILoadPrescription;
import com.colsubsidio.pm.load.prescription.enums.QueveRabbitmqEnum;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Ingeneo
 */

@Log4j2
@Component
public class PMReceive {
    
    @Autowired
    private ILoadPrescription loadPrescriptionAuthorizedNeps;


    @Value( "${colsubsidio.rabbitmq.host}" )
    private String rabbitHost;
    @Value( "${colsubsidio.rabbitmq.virtual-host}" )
    private String virtualHost;
    @Value( "${colsubsidio.rabbitmq.username}" )
    private String username;
    @Value( "${colsubsidio.rabbitmq.password}" )
    private String password;
    @Value( "${colsubsidio.rabbitmq.port}" )
    private Integer rabbitPort;
    @Value( "${colsubsidio.rabbitmq.ssl.enabled}" )
    private Boolean activeSSL;

    private static final Integer SECURE_PORT = 5671;
    private static final String SSLCONTEXT_TLSV12 = "TLSv1.2";
    private static final Integer RECOVERY_INTERNAL = 60000;

    private final Long timeoutmax = 500L;

    private static HashMap<String, String> QUEUES;

    public PMReceive() {
        PMReceive.QUEUES = new HashMap<>();
    }

    /**
     * Recibe datos de RabbitMQ
     *
     * @param exchange
     * @throws Exception
     */
    public void receives( QueveRabbitmqEnum exchange )
        throws Exception {

        String queueName;

        queueName = PMReceive.QUEUES.get( exchange.name() );

        if( queueName == null ) {
            PMReceive.QUEUES.put( exchange.name(), exchange.name() );
            this.basicConsumer( exchange.name() );
        }
    }

    private void basicConsumer( String exchange )
        throws Exception {

        ConnectionFactory factory;

        factory = new ConnectionFactory();
        factory.setHost( rabbitHost );
        factory.setVirtualHost( virtualHost );
        factory.setPort( rabbitPort );
        factory.setUsername( username );
        factory.setPassword( password );

        factory.setAutomaticRecoveryEnabled( true );
        factory.setNetworkRecoveryInterval( RECOVERY_INTERNAL );
        factory.setRequestedHeartbeat( ConnectionFactory.DEFAULT_HEARTBEAT );

        if( SECURE_PORT == factory.getPort() && activeSSL ) {
            factory.useSslProtocol( SSLCONTEXT_TLSV12 );
        }

        Connection connection;
        connection = factory.newConnection( exchange );
        Channel channel = connection.createChannel();
        channel.queueDeclare( exchange, true, false, false, null );

        final boolean autoAck = true;

        log.trace( "[*] Waiting for messages. To exit press CTRL+C" );

        registerConsumer( exchange, channel, autoAck, this.timeoutmax );
    }

    private void registerConsumer( String queueName, final Channel channel, final boolean autoAck, final Long timeout )
        throws IOException {

        Consumer consumer;
        consumer = new DefaultConsumer( channel ) {

            @Override
            public void handleDelivery( String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                        byte[] body )
                throws IOException {

                try {

                    String message = new String( body, "UTF-8" );
                    log.info( "[x] recibiendo (channel {}) {}", channel.getChannelNumber(), new String( body ) );

                    Long tm, initial, end;

                    tm = timeout;

                    initial = System.currentTimeMillis();
                    //Se convierte String en el valor del enum PrescriptionState
                    QueveRabbitmqEnum prescriptionState = QueveRabbitmqEnum.valueOf( queueName );

                    switch( prescriptionState ) {
                        case PRESCRIPCION_PBS :
                            loadPrescriptionAuthorizedNeps.savePrescription( message );
                            break;

                    }

                    end = System.currentTimeMillis();

                    tm = end - initial;

                    log.info( "El proceso se demoro {} ms", tm );

                    if( !autoAck ) {
                        channel.basicAck( envelope.getDeliveryTag(), false );
                    }

                }
                catch( Exception ex ) {
                    log.error( "Error al tratar de procesar el message " +
                                  new String( body, "UTF-8" ) + " del queue " +
                                  queueName, ex );
                }
            }

        };

        channel.basicConsume( queueName, autoAck, consumer );

    }

}
