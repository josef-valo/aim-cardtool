/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ca.szc.configparser.Ini;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Parsing card readers configuration.
 * 
 * Python configuration is used for card reader configuration.
 * 
 * @author Josef Valo
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ReaderConfigService {
    private static final String DEFAULT = "DEFAULT";
    private static final String HOST = "host";
    /** Index card reader */
    private static final String READER_NO = "readerno";

    private final NetworkInterfaceService newtworkService;

    /**
     * Parses card readers configuration.
     * 
     * @return card reader names mapped to card reader indexes.
     * @throws IOException When errors are encountered while reading configuration
     *                     file.
     */
    public Map<Short, String> readConfig(Path path) throws IOException {
        final Ini ini = new Ini().read(path);
        final Map<String, Map<String, String>> sections = ini.getSections();

        return getReaderConfig(sections);
    }

    /**
     * Reads reader configuration.
     */
    protected Map<Short, String> getReaderConfig(final Map<String, Map<String, String>> sections) {
        final Map<Short, String> result = new HashMap<>();

        final List<String> addresses = newtworkService.getIpAddress();

        for (final Map.Entry<String, Map<String, String>> entry : sections.entrySet()) {
            final String key = entry.getKey();

            // Ignore "default" configuration section
            if (!DEFAULT.equals(key)) {
                final Short readNo = Short.valueOf(entry.getValue().get(READER_NO));
                final String host = entry.getValue().get(HOST);

                try {
                    if (addresses.contains(newtworkService.resolveIP(host))) {
                        result.put(readNo, entry.getKey());
                    }
                } catch (UnknownHostException e) {
                    log.warn(host + " was not found.", e);
                }
            }
        }

        return result;
    }
}
