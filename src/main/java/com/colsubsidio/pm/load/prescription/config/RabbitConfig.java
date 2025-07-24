/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.config;

import com.colsubsidio.utilities.miscellaneous.rabbit.models.RabbitConnectionDto;
import com.colsubsidio.utilities.miscellaneous.rabbit.models.RabbitConnectionExchangeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author Robert Barraza
 */
@Component
public class RabbitConfig {
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

    @Value( "${colsubsidio.rabbitmq.recovery.internal}" )
    private Integer recoveryInternal;

    @Value( "${colsubsidio.rabbitmq.amountConsumerLimit}" )
    private Integer amountConsumerLimit;

    @Value( "${colsubsidio.rabbitmq.numConsumer}" )
    private Integer numConsumer;

    @Value( "${colsubsidio.rabbitmq.ssl.port}" )
    private Integer securePort;

    @Value( "${colsubsidio.rabbitmq.autoAck}" )
    private Boolean autoAck;

    @Value( "${colsubsidio.rabbitmq.connection.name}" )
    private String nameConnection;

    @Value( "${colsubsidio.rabbitmq.connection.exchange}" )
    private String nameConnectionExchange;

    @Autowired
    private Environment env;

    public RabbitConnectionDto getConfigConection() {
        return RabbitConnectionDto.builder()
            .activeSSL( this.activeSSL )
            .rabbitHost( this.rabbitHost )
            .rabbitPort( this.rabbitPort )
            .password( this.password )
            .username( this.username )
            .virtualHost( this.virtualHost )
            .numConsumer( this.numConsumer )
            .autoAck( this.autoAck )
            .securePort( this.securePort )
            .recoveryInternal( this.recoveryInternal )
            .amountConsumerLimit( this.amountConsumerLimit )
            .nameConnection( this.nameConnection ).build();
    }

 
    public RabbitConnectionExchangeDto getConfigConectionExchange( String nameExchange ) {

        RabbitConnectionExchangeDto rabbitConnectionExchangeDto;
        String exchangeKey, exchange;
        rabbitConnectionExchangeDto = new RabbitConnectionExchangeDto();
            rabbitConnectionExchangeDto.setActiveSSL( this.activeSSL );
            rabbitConnectionExchangeDto.setRabbitHost( this.rabbitHost );
            rabbitConnectionExchangeDto.setRabbitPort( this.rabbitPort );
            rabbitConnectionExchangeDto.setPassword( this.password );
            rabbitConnectionExchangeDto.setUsername( this.username );
            rabbitConnectionExchangeDto.setVirtualHost( this.virtualHost );
            rabbitConnectionExchangeDto.setNumConsumer( this.numConsumer );
            rabbitConnectionExchangeDto.setAutoAck( this.autoAck );
            rabbitConnectionExchangeDto.setSecurePort( this.securePort );
            rabbitConnectionExchangeDto.setRecoveryInternal( this.recoveryInternal );
            rabbitConnectionExchangeDto.setAmountConsumerLimit( this.amountConsumerLimit );
            rabbitConnectionExchangeDto.setNameConnection( this.nameConnectionExchange );

        exchangeKey = this.env.getProperty( "colsubsidio.rabbitmq." + nameExchange.toLowerCase() + ".key" );
        exchange = this.env.getProperty( "colsubsidio.rabbitmq." +
                                         nameExchange.toLowerCase() + ".exchange" );

        rabbitConnectionExchangeDto.setExchange( exchange );
        rabbitConnectionExchangeDto.setExchangeKey( exchangeKey );

        return rabbitConnectionExchangeDto;
    }

}