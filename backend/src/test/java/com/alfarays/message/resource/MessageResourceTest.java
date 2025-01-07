package com.alfarays.message.resource;

import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import com.alfarays.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageResourceTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageResource messageResource;

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
    void create_ShouldReturnCreatedMessage() {
        when(messageService.create(any(MessageRequest.class))).thenReturn(response);

        ResponseEntity<MessageResponse> result = messageResource.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(messageService).create(request);
    }

    @Test
    void findById_ShouldReturnMessage() {
        when(messageService.findById(testId)).thenReturn(response);

        ResponseEntity<MessageResponse> result = messageResource.findById(testId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(messageService).findById(testId);
    }

    @Test
    void findAll_ShouldReturnAllMessages() {
        List<MessageResponse> messages = Arrays.asList(response);
        when(messageService.findAll()).thenReturn(messages);

        ResponseEntity<List<MessageResponse>> result = messageResource.findAll();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messages, result.getBody());
        verify(messageService).findAll();
    }

    @Test
    void update_ShouldReturnUpdatedMessage() {
        when(messageService.update(eq(testId), any(MessageRequest.class))).thenReturn(response);

        ResponseEntity<MessageResponse> result = messageResource.update(testId, request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(messageService).update(testId, request);
    }

    @Test
    void deleteById_ShouldReturnNoContent() {
        doNothing().when(messageService).deleteById(testId);

        ResponseEntity<Void> result = messageResource.deleteById(testId);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(messageService).deleteById(testId);
    }
}