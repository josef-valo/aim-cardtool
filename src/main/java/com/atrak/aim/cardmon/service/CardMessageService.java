/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
/**
 * @author Josef Valo
 */
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

import lombok.RequiredArgsConstructor;

/**
 * Processing of the received cardmon messages.
 * 
 * @author Josef Valo
 */
@RequiredArgsConstructor
@Service
public class CardMessageService {

    private final CardReaderService service;

    /**
     * Processes inbound message from cardmon. 
     * Only {@link MsgType#GET_STATUS} is supported.
     * 
     * @return message with card status for sending into cardmon.
     * @throws IllegalArgumentException if message type is not supported.
     */
    @ServiceActivator
    public Message<CardMsg> processMessage(MessageHeaders headers, Message<CardMsg> msg) {
        final CardMsg payload = msg.getPayload();

        if (payload.getType() != MsgType.GET_STATUS) {
            throw new IllegalArgumentException("Unssuported type message: " + payload.getType());
        }

        final short index = payload.getReaderNo();
        final String cardId = service.cardState(index);

        final CardMsg.CardMsgBuilder builder = CardMsg.builder();
        if (StringUtils.hasText(cardId)) {
            builder.type(MsgType.CARD_PRESENT);
        } else {
            builder.type(MsgType.CARD_EMPTY);
        }

        builder.readerNo((byte) index).cardId(cardId).build();

        return MessageBuilder.withPayload(builder.build()).build();
    }

}