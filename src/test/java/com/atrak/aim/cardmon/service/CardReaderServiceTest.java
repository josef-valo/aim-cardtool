/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atrak.aim.cardmon.config.TcpConfiguration.MsgGateway;

/**
 * @author Josef Valo
 */
@ExtendWith(MockitoExtension.class)
class CardReaderServiceTest {

    @Mock
    MsgGateway msgGateway;

    CardReaderService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new CardReaderService((short) 2, msgGateway);
        service.postConstruct();
    }

    @Test
    void testCardStatuses() {
        final String cardId = "CARD_ID";

        // card ID
        String result = service.cardIn((short) 1, cardId);
        assertEquals(cardId, result);
        assertEquals(cardId, service.cardState((short) 1));
        assertTrue(service.cardState((short) 2).isEmpty());

        // card states
        final Map<Short, String> states = service.cardStates();
        assertEquals(2, states.size());
        assertEquals(cardId, states.get((short) 1));
        assertTrue(states.get((short) 2).isEmpty());

        // card OUT
        service.cardOut((short) 1, cardId);
        assertTrue(service.cardState((short) 1).isEmpty());
        assertTrue(service.cardState((short) 2).isEmpty());
    }

    @Test
    void testExcption() {
        assertThrows(IllegalArgumentException.class, () -> service.cardIn((short) 3, ""));
        assertThrows(IllegalArgumentException.class, () -> service.cardOut((short) 0, ""));
        assertThrows(IllegalArgumentException.class, () -> service.cardState((short) 5));
    }

}
