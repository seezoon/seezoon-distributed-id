package com.seezoon.interfaces.web;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class DistributedIdControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void get() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/get/{bizTag}", "test").param("token", "1"))
                .andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.debug("response:{}", contentAsString);
    }
}