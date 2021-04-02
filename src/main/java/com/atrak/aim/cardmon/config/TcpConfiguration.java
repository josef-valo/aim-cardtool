/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.dsl.TcpClientConnectionFactorySpec;
import org.springframework.integration.ip.dsl.TcpInboundChannelAdapterSpec;
import org.springframework.integration.ip.dsl.TcpOutboundChannelAdapterSpec;
import org.springframework.integration.ip.dsl.TcpServerConnectionFactorySpec;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.messaging.MessageChannel;

import com.atrak.aim.cardmon.convert.ByteToCardMsgTransformer;
import com.atrak.aim.cardmon.convert.CardMsgToByteTransformer;
import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.service.CardMessageService;

/**
 * Configuration TCP/IP communication.
 * 
 * Messages from/to cardmon are received/sent via TCP/IP protocol. For every
 * direction is created own one way TCP/IP communication.
 * 
 * @author Josef Valo
 */
@EnableIntegration
@Configuration
public class TcpConfiguration {

    /** Local TCP/IP server port */
    @Value("${tcp.server.port}")
    private int serverPort;

    /** Remote TCP/IP client port */
    @Value("${tcp.client.port}")
    private int clientPort;

    /** Remote TCP/IP address */
    @Value("${tcp.client.address}")
    private String clientAddress;
    
    /** Remote TCP/IP connection timeout */
    @Value("${tcp.client.timeout:2}")
    private int clientTimeout;

    /**
     * Receiving messages from cardmon via TCP/IP protocol. Messages are transformed
     * from byte representation into {@link CardMsg} object.
     */
    @Bean
    public IntegrationFlow fromCardmon() {
        return IntegrationFlows.from(inboundAdapter())
                .transform(toCardMsgTranformer())
                .log(LoggingHandler.Level.INFO, m -> "Message received: " + m)
                .channel(fromCardmonChannel())
                .get();
    }

    @Bean
    public TcpInboundChannelAdapterSpec inboundAdapter() {
        return Tcp.inboundAdapter(tcpServer());
    }

    @Bean
    public TcpServerConnectionFactorySpec tcpServer() {
        return Tcp.netServer(serverPort).deserializer(TcpCodecs.lengthHeader2()); // 2 bytes header
    }
    
    /**
     * Processing received cardmon messages.
     */
    @Bean
    public IntegrationFlow processCardmon(CardMessageService cardMsgService) {
        return IntegrationFlows.from(fromCardmonChannel())
                .handle(cardMsgService)
                .channel(toCardmonChannel())
                .get();
    }

    /**
     * Sending messages to cardmon via TCP/IP protocol .Messages are transformed
     * from {@link CardMsg} object into byte representation.
     */
    @Bean
    public IntegrationFlow toCardmon() {
        return IntegrationFlows.from(toCardmonChannel())
                .log(LoggingHandler.Level.INFO, m -> "Message send: " + m)
                .transform(toByteMsgTranformer())
                .handle(outboundAdapter())
                .get();
    }

    @Bean
    public TcpOutboundChannelAdapterSpec outboundAdapter() {
        return Tcp.outboundAdapter(tcpClient());
    }

    @Bean
    public TcpClientConnectionFactorySpec tcpClient() {
        return Tcp.netClient(clientAddress, clientPort)
                // close connection after sent message (API specification)
                .leaveOpen(false)
                .connectTimeout(clientTimeout)
                .serializer(TcpCodecs.lengthHeader2());
    }

    @Bean
    public ByteToCardMsgTransformer toCardMsgTranformer() {
        return new ByteToCardMsgTransformer();
    }
    
    
    @Bean
    public CardMsgToByteTransformer toByteMsgTranformer() {
        return new CardMsgToByteTransformer();
    }

    /**
     * Message channel with received decoded messages. 
     */
    @Bean
    public MessageChannel fromCardmonChannel() {
        return new DirectChannel();
    }
    
    /**
     * Message channel for sending messages. 
     */
    @Bean
    public MessageChannel toCardmonChannel() {
        return new DirectChannel();
    }
    
    /**
     * Message gateway for sending message from services. 
     */
    @MessagingGateway(defaultRequestChannel = "toCardmonChannel")
    public interface MsgGateway {

        void sendMsg(CardMsg msg);
    }
}
