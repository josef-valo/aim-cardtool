/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

/**
 * @author Josef Valo
 */
@ExtendWith(MockitoExtension.class)
class CardMessageServiceTest {

    @Mock
    CardReaderService cardService;
    
    CardMessageService service;
    
    @BeforeEach
    void setUp() {
        service = new CardMessageService(cardService);
    }

    @Test
    void testProcessMessageCardPresent() {
        final Message<CardMsg> msg = MessageBuilder
                .withPayload(CardMsg.builder()
                        .type(MsgType.GET_STATUS)
                        .readerNo((byte) 2)
                        .build()
                 ).build();
        when(cardService.cardState((short) 2)).thenReturn("CARD_ID");

        final CardMsg result = service.processMessage(null, msg).getPayload();

        assertEquals(MsgType.CARD_PRESENT, result.getType());
        assertEquals((short) 2, result.getReaderNo());
        assertEquals("CARD_ID", result.getCardId());
    }
    
    @Test
    void testProcessMessageCardEmpty() {
        final Message<CardMsg> msg = MessageBuilder
                .withPayload(CardMsg.builder()
                        .type(MsgType.GET_STATUS)
                        .readerNo((byte) 2)
                        .build()
                 ).build();
        when(cardService.cardState((short) 2)).thenReturn("");

        final CardMsg result = service.processMessage(null, msg).getPayload();

        assertEquals(MsgType.CARD_EMPTY, result.getType());
        assertEquals((short) 2, result.getReaderNo());
        assertEquals("", result.getCardId());
    }
    
    @Test
    void testProcessMessageUnssuportedType() {
        final Message<CardMsg> msg = MessageBuilder
                .withPayload(CardMsg.builder()
                        .type(MsgType.NONE)
                        .readerNo((byte) 2)
                        .build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> service.processMessage(null, msg));
    }

}
