/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

/**
 * @author Josef Valo
 *
 */
class ByteToCardMsgTransformerTest {
    private final ByteToCardMsgTransformer transformer = new ByteToCardMsgTransformer();
    private final MessageHeaders headers = new MessageHeaders(new HashMap<>());

    @Test
    void testConvertIncorrectSize() {
        final Message<byte[]> emptyMsg = MessageBuilder.withPayload(new byte[0]).build();

        assertThrows(IllegalArgumentException.class, () -> transformer.convert(headers, emptyMsg));

        final Message<byte[]> longMsg = MessageBuilder.withPayload(new byte[3]).build();
        assertThrows(IllegalArgumentException.class, () -> transformer.convert(headers, longMsg));
    }

    @Test
    void testConvertUnsupporated() {
        final byte[] data = new byte[2];
        data[0] = (byte) 50;
        data[1] = (byte) 5;

        final Message<byte[]> unsupportedMsg = MessageBuilder.withPayload(data).build();
        assertThrows(IllegalArgumentException.class, () -> transformer.convert(headers, unsupportedMsg));
    }

    @Test
    void testConvert() {
        final byte[] data = new byte[2];
        data[0] = MsgType.GET_STATUS.getIndex();
        data[1] = (byte) 5;

        final Message<CardMsg> result = transformer.convert(headers, MessageBuilder.withPayload(data).build());
        assertEquals(MsgType.GET_STATUS, result.getPayload().getType());
        assertEquals(5, result.getPayload().getReaderNo());
    }

}
