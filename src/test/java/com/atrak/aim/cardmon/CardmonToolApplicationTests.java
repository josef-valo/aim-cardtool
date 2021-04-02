package com.atrak.aim.cardmon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.atrak.aim.cardmon.config.TcpConfiguration.MsgGateway;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CardmonToolApplicationTests {

    @MockBean
    private MsgGateway gateway;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void insertAndRemoteCard() throws Exception {
        // insert card
        mockMvc.perform(MockMvcRequestBuilders.post("/card/1/CARD_ID"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("CARD_ID"));

        // get card
        mockMvc.perform(MockMvcRequestBuilders.get("/card/1")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("CARD_ID"));

        // get cards
        mockMvc.perform(MockMvcRequestBuilders.get("/card")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"1\":\"CARD_ID\",\"2\":\"\"}"));
        
        // remote card
        mockMvc.perform(MockMvcRequestBuilders.delete("/card/1")).andExpect(MockMvcResultMatchers.status().isOk());

        // get card
        mockMvc.perform(MockMvcRequestBuilders.get("/card/1")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

}
