/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.convert;

import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

/**
 * Decoder of the cardmon messages.
 * 
 * @author Josef Valo
 */
public class ByteToCardMsgTransformer {

    @Transformer
    public Message<CardMsg> convert(MessageHeaders headers, Message<byte[]> m) {
        final CardMsg.CardMsgBuilder builder = CardMsg.builder();
        final byte[] data = m.getPayload();

        if (data.length != 2) {
            throw new IllegalArgumentException("Incorrect length of message: length=" + data.length);
        }

        if (data[0] == MsgType.GET_STATUS.getIndex()) {
            builder.type(MsgType.GET_STATUS);
        } else {
            throw new IllegalArgumentException("Unsupported type of the message: type=" + data[0]);
        }

        builder.readerNo(data[1]);

        return MessageBuilder.withPayload(builder.build()).copyHeaders(headers).build();
    }
}