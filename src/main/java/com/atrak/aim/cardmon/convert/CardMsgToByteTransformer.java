/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.convert;

import java.util.Arrays;

import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

/**
 * Encoder of the Cardmon messages.
 *
 * @author Josef Valo
 */
public class CardMsgToByteTransformer {

    @Transformer
    public Message<byte[]> convert(MessageHeaders headers, Message<CardMsg> msg) {
        final byte[] result = new byte[34];

        final CardMsg payload = msg.getPayload();

        result[0] = payload.getType().getIndex();
        result[1] = payload.getReaderNo();

        // Initialize cardId to ' '
        Arrays.fill(result, 2, result.length-1, (byte)' ');
        
        if (payload.getType() != MsgType.CARD_EMPTY && StringUtils.hasText(payload.getCardId())) {
            byte[] cardCode = payload.getCardId().getBytes();
            System.arraycopy(cardCode, 0, result, result.length - cardCode.length, cardCode.length);
        }

        if (payload.getType() == MsgType.CARD_EMPTY) {
            return MessageBuilder.withPayload(Arrays.copyOf(result, 2)).copyHeadersIfAbsent(msg.getHeaders()).build();
        }

        return MessageBuilder.withPayload(result).copyHeaders(headers).build();
    }
}