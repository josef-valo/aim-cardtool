/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.controller;

import java.util.Map;

import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atrak.aim.cardmon.service.CardReaderService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

/**
 * Rest API for simulation card readers.
 */
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/card")
public class CardController {

    private final CardReaderService service;

    @Operation(summary = "Get stuatuses all card readers.")
    @GetMapping
    public ResponseEntity<Map<Short, String>> getAllCardReaders() {

        return new ResponseEntity<>(service.cardStates(), HttpStatus.OK);
    }

    @Operation(summary = "Get stuatus of the card reader.")
    @GetMapping(path = "/{readerNo}")
    public ResponseEntity<String> getCardReader(@PathVariable short readerNo) {

        return new ResponseEntity<>(service.cardState(readerNo), HttpStatus.OK);
    }

    @Operation(summary = "Insert card into card reader.")
    @PostMapping(path = "/{readerNo}/{cardId}")
    public ResponseEntity<String> cardInsert(@PathVariable short readerNo,
            @PathVariable @Size(max = 32) String cardId) {

        return new ResponseEntity<>(service.cardIn(readerNo, cardId), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove card into card reader.")
    @DeleteMapping(path = "/{readerNo}")
    public ResponseEntity<Void> cardDelete(@PathVariable short readerNo) {

        service.cardOut(readerNo, service.cardState(readerNo));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}