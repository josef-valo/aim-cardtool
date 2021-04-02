/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.atrak.aim.cardmon.model.CardMsg;

/**
 * @author Josef Valo
 *
 */
class CardMsgToByteTransformerTest {
    private final CardMsgToByteTransformer transformer = new CardMsgToByteTransformer();
    private final MessageHeaders headers = new MessageHeaders(new HashMap<>());

    @Test
    void testCardIn() {
        final CardMsg data = CardMsg.builder().type(CardMsg.MsgType.CARD_IN).readerNo((byte) 10).cardId("ID").build();
        final Message<byte[]> result = transformer.convert(headers, MessageBuilder.withPayload(data).build());
        final byte[] payload = result.getPayload();

        assertEquals(34, payload.length);
        assertEquals(CardMsg.MsgType.CARD_IN.getIndex(), payload[0]);
        assertEquals((byte) 10, payload[1]);

        final byte[] expectedtId = new byte[32];
        Arrays.fill(expectedtId, (byte) ' ');
        expectedtId[30] = 'I';
        expectedtId[31] = 'D';
        assertArrayEquals(expectedtId, Arrays.copyOfRange(payload, 2, payload.length));
    }

    @Test
    void testCardOut() {
        final CardMsg data = CardMsg.builder().type(CardMsg.MsgType.CARD_OUT).readerNo((byte) 6).cardId("ID").build();
        final Message<byte[]> result = transformer.convert(headers, MessageBuilder.withPayload(data).build());
        final byte[] payload = result.getPayload();

        assertEquals(34, payload.length);
        assertEquals(CardMsg.MsgType.CARD_OUT.getIndex(), payload[0]);
        assertEquals((byte) 6, payload[1]);

        final byte[] expectedtId = new byte[32];
        Arrays.fill(expectedtId, (byte) ' ');
        expectedtId[30] = 'I';
        expectedtId[31] = 'D';
        assertArrayEquals(expectedtId, Arrays.copyOfRange(payload, 2, payload.length));
    }

    @Test
    void testCardPresent() {
        final CardMsg data = CardMsg.builder().type(CardMsg.MsgType.CARD_PRESENT).readerNo((byte) 7).cardId("ID")
                .build();
        final Message<byte[]> result = transformer.convert(headers, MessageBuilder.withPayload(data).build());
        final byte[] payload = result.getPayload();

        assertEquals(34, payload.length);
        assertEquals(CardMsg.MsgType.CARD_PRESENT.getIndex(), payload[0]);
        assertEquals((byte) 7, payload[1]);

        final byte[] expectedtId = new byte[32];
        Arrays.fill(expectedtId, (byte) ' ');
        expectedtId[30] = 'I';
        expectedtId[31] = 'D';
        assertArrayEquals(expectedtId, Arrays.copyOfRange(payload, 2, payload.length));
    }

    @Test
    void testCardEmpty() {
        final CardMsg data = CardMsg.builder().type(CardMsg.MsgType.CARD_EMPTY).readerNo((byte) 7).build();
        final Message<byte[]> result = transformer.convert(headers, MessageBuilder.withPayload(data).build());
        final byte[] payload = result.getPayload();

        assertEquals(2, payload.length);
        assertEquals(CardMsg.MsgType.CARD_EMPTY.getIndex(), payload[0]);
        assertEquals((byte) 7, payload[1]);
    }

}
