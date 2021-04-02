/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.atrak.aim.cardmon.model.CardReader;
import com.atrak.aim.cardmon.service.CardReaderService;
import com.atrak.aim.cardmon.service.ReaderConfigService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Josef Valo
 */
@Slf4j
@Controller
public class CardWebConroller {

    private static final String ERROR_MSG = "error_msg";

    private final Resource configFile;
    private final CardReaderService service;
    private final ReaderConfigService configService;

    private Map<Short, String> readerConfig;

    @Autowired
    public CardWebConroller(@Value("${cardmon.reader.config}") Resource configFile, CardReaderService service,
            ReaderConfigService configService, Map<Short, String> readerConfig) {
        super();
        this.configFile = configFile;
        this.service = service;
        this.configService = configService;
        this.readerConfig = readerConfig;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        readReaderConfig();
    }

    private void readReaderConfig() throws IOException {
        readerConfig = new ConcurrentHashMap<>();

        if (configFile.exists()) {
            final Path file = Paths.get(configFile.getURI());
            readerConfig.putAll(configService.readConfig(file));
        } else {
            log.error("Card reader configuration file was not found: " + configFile.getURI());
            throw new IOException("Card reader configuration file was not found: " + configFile.getURI());
        }
    }

    @GetMapping(value = "/")
    public String cardReaders(Model model) {
        final Map<Short, String> cards = service.cardStates();
        final Map<Short, CardReader> readers = new HashMap<>();

        for (Entry<Short, String> e : cards.entrySet()) {
            final CardReader.CardReaderBuilder builder = CardReader.builder();
            builder.readerNo(e.getKey()).cardId(e.getValue());

            if (readerConfig.containsKey(e.getKey())) {
                builder.name(readerConfig.get(e.getKey()));
            }

            readers.put(e.getKey(), builder.build());
        }

        model.addAttribute("readers", readers);

        return "index";
    }

    @PostMapping("/")
    public RedirectView updateCard(final RedirectAttributes redirectAttributes, @RequestParam short readerNo, @RequestParam String cardId) {

        final RedirectView redirectView = new RedirectView("/", true);
        
        try {
            if (StringUtils.hasText(cardId)) {
                service.cardIn(readerNo, cardId.toUpperCase());
            } else {
                final String card = service.cardState(readerNo);
                if (StringUtils.hasText(card)) {
                    service.cardOut(readerNo, card);
                }
            }
        } catch (UncheckedIOException e) {
            redirectAttributes.addFlashAttribute(ERROR_MSG, "Message was not sent into cardmon: " + e.getMessage());
            
            log.error("Message was not sent into cardmon.", e);
        }

        return redirectView;
    }

    @GetMapping(value = "/config/refresh")
    public RedirectView refresConfig(final RedirectAttributes redirectAttributes) {
        final RedirectView redirectView = new RedirectView("/", true);

        try {
            readReaderConfig();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(ERROR_MSG, "Configuration was not parsed: " + e.getMessage());
            
            log.error("Configuration was not parsed.", e);
        }

        return redirectView;
    }

}
