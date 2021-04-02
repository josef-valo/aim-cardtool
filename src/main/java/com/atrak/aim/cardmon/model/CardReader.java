/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Josef Valo
 */
@AllArgsConstructor
@Builder
@Getter
public class CardReader {
    private final int readerNo;
    @Builder.Default
    private final String name = "";
    @Builder.Default
    private final String cardId = "";
}