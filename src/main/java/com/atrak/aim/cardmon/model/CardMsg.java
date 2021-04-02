/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Cardmon message.
 * 
 * @author Josef Valo
 */
@Getter
@ToString
@AllArgsConstructor
@Builder
public class CardMsg {
    @Builder.Default
    private final MsgType type = MsgType.NONE;
    private final byte readerNo;
    private final String cardId;

    public enum MsgType {
        NONE((byte) 0),
        GET_STATUS((byte) 5), 
        CARD_IN((byte) 6), 
        CARD_OUT((byte) 7), 
        CARD_PRESENT((byte) 8),
        CARD_EMPTY((byte) 9);

        @Getter
        final byte index;

        private MsgType(byte index) {
            this.index = index;
        }
    }

}
