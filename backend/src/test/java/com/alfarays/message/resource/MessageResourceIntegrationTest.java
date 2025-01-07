package com.alfarays.message.resource;

import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import com.alfarays.message.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageResource.class)
class MessageResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MessageRequest request;
    private MessageResponse response;
    private Long testId;

    @BeforeEach
    void setUp() {
        testId = 1L;
        request = new MessageRequest();
        response = new MessageResponse();
        response.setId(testId);
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        when(messageService.create(any(MessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        when(messageService.findById(testId)).thenReturn(response);

        mockMvc.perform(get("/messages/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void findAll_ShouldReturn200() throws Exception {
        when(messageService.findAll()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testId));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        when(messageService.update(eq(testId), any(MessageRequest.class))).thenReturn(response);

        mockMvc.perform(put("/messages/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void deleteById_ShouldReturn204() throws Exception {
        doNothing().when(messageService).deleteById(testId);

        mockMvc.perform(delete("/messages/{id}", testId))
                .andExpect(status().isNoContent());
    }
}