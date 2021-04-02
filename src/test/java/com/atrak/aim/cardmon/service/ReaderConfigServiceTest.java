/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Josef Valo
 *
 */
@ExtendWith(MockitoExtension.class)
class ReaderConfigServiceTest {

    @Mock
    NetworkInterfaceService newtworkService;

    @Test
    void test() throws IOException {
        final Path path = new ClassPathResource("test_reader_config.cfg").getFile().toPath();

        when(newtworkService.getIpAddress()).thenReturn(Arrays.asList("127.0.0.1"));
        when(newtworkService.resolveIP(anyString())).thenReturn("127.0.0.1");

        final ReaderConfigService service = new ReaderConfigService(newtworkService);
        final Map<Short, String> result = service.readConfig(path);

        assertEquals(2, result.size());
        assertEquals("ACC_1_PC_MAIN", result.get((short) 1));
        assertEquals("ACC_1_EC_MAIN", result.get((short) 2));
    }

}
