/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atrak.aim.cardmon.config.TcpConfiguration.MsgGateway;
import com.atrak.aim.cardmon.model.CardMsg;
import com.atrak.aim.cardmon.model.CardMsg.MsgType;

import lombok.extern.slf4j.Slf4j;

/**
 * Service holds number of cards inserted into readers.
 * 
 * @author Josef Valo
 */
@Service
@Slf4j
public class CardReaderService {

    private static final String ERROR_MSG = "ReaderNo has to be between 1 and ";

    private final short readersCount;
    private final MsgGateway msgGateway;

    /** Holding card id numbers, key is readerNo */
    private final Map<Short, String> readersStatus = new ConcurrentHashMap<>();

    @Autowired
    public CardReaderService(@Value("${cardmon.reader.count}") short readersCount, MsgGateway msgGateway) {
        this.readersCount = readersCount;
        this.msgGateway = msgGateway;
    }

    @PostConstruct
    void postConstruct() {
        IntStream.range(1, readersCount + 1).forEach(i -> readersStatus.put((short) i, ""));
    }

    /**
     * Simulates inserting card into card reader.
     * 
     * {@link MsgType#CARD_IN} is sent into cardmon.
     *
     * @param readerNo index of the card reader
     * @param cardId   card id number
     * 
     * @return card id number
     * @throws IllegalArgumentException if readerNo is outside range.
     */
    public String cardIn(short readerNo, String cardId) {
        if (!readersStatus.containsKey(readerNo)) {
            throw new IllegalArgumentException(ERROR_MSG + readersCount);
        }

        readersStatus.put(readerNo, cardId);

        log.info("Card inserted: readerNo = " + readerNo + " cardId = " + cardId);

        msgGateway.sendMsg(CardMsg.builder().type(MsgType.CARD_IN).readerNo((byte) readerNo).cardId(cardId).build());

        return cardId;
    }

    /**
     * Simulates removing card from card reader.
     * 
     * {@link MsgType#CARD_OUT} is sent into cardmon.
     *
     * @param readerNo index of the card reader
     * @param cardId   card id number
     * 
     * @return card id number
     * @throws IllegalArgumentException if readerNo is outside range.
     * 
     */
    public void cardOut(short readerNo, String cardId) {
        if (!readersStatus.containsKey(readerNo)) {
            throw new IllegalArgumentException(ERROR_MSG + readersCount);
        }

        readersStatus.put(readerNo, "");

        log.info("Card removed: readerNo = " + readerNo + " cardId = " + cardId);

        msgGateway.sendMsg(CardMsg.builder().type(MsgType.CARD_OUT).readerNo((byte) readerNo).cardId(cardId).build());
    }

    /**
     * Gets card id number for card reader.
     * 
     * @param readerNo index of the card reader
     * 
     * @throws IllegalArgumentException if readerNo is outside range.
     * 
     */
    public String cardState(short readerNo) {
        if (!readersStatus.containsKey(readerNo)) {
            throw new IllegalArgumentException(ERROR_MSG + readersCount);
        }

        return readersStatus.get(readerNo);
    }

    /**
     * Gets all card id numbers mapped into index readers.
     */
    public Map<Short, String> cardStates() {
        // sorted map by key
        return new TreeMap<>(readersStatus);
    }

}
