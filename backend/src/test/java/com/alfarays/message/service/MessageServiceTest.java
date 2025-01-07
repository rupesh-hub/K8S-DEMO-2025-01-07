package com.alfarays.message.service;

import com.alfarays.message.entity.Message;
import com.alfarays.message.mapper.MessageMapper;
import com.alfarays.message.model.MessageRequest;
import com.alfarays.message.model.MessageResponse;
import com.alfarays.message.repository.MessageRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    private static final String TEST_MESSAGE = "Hello World!";
    private static final String TEST_SENDER = "Alice";
    private static final String TEST_RECEIVER = "Bob";
    private static final String TEST_TIMESTAMP = "123456789";
    private static final Long TEST_ID = 1L;

    private MessageRequest createTestRequest() {
        return MessageRequest.builder()
                .message(TEST_MESSAGE)
                .sender(TEST_SENDER)
                .receiver(TEST_RECEIVER)
                .build();
    }

    private Message createTestMessage() {
        return Message.builder()
                .id(TEST_ID)
                .message(TEST_MESSAGE)
                .sender(TEST_SENDER)
                .receiver(TEST_RECEIVER)
                .timestamp(TEST_TIMESTAMP)
                .build();
    }

    private MessageResponse createTestResponse() {
        return MessageResponse.builder()
                .message(TEST_MESSAGE)
                .sender(TEST_SENDER)
                .receiver(TEST_RECEIVER)
                .build();
    }

    @Nested
    class CreateTests {
        @Test
        void shouldReturnMessageResponse_whenValidRequest() {
            MessageRequest request = createTestRequest();
            Message savedMessage = createTestMessage();

            try (MockedStatic<MessageMapper> mapperMock = Mockito.mockStatic(MessageMapper.class)) {
                ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

                mapperMock.when(() -> MessageMapper.toEntity(request))
                        .thenReturn(Message.builder()
                                .message(TEST_MESSAGE)
                                .sender(TEST_SENDER)
                                .receiver(TEST_RECEIVER)
                                .build());

                when(messageRepository.save(messageCaptor.capture())).thenReturn(savedMessage);
                mapperMock.when(() -> MessageMapper.toResponse(savedMessage)).thenReturn(createTestResponse());

                MessageResponse response = messageService.create(request);

                assertMessageResponse(response);
                verifyTimestamp(messageCaptor.getValue());
                verify(messageRepository).save(any(Message.class));
                mapperMock.verify(() -> MessageMapper.toEntity(request));
                mapperMock.verify(() -> MessageMapper.toResponse(savedMessage));
            }
        }

        @Test
        void shouldReturnNull_whenRepositoryReturnsNull() {
            MessageRequest request = createTestRequest();

            try (MockedStatic<MessageMapper> mapperMock = Mockito.mockStatic(MessageMapper.class)) {
                mapperMock.when(() -> MessageMapper.toEntity(request))
                        .thenReturn(createTestMessage());
                when(messageRepository.save(any(Message.class))).thenReturn(null);

                assertNull(messageService.create(request));
                verify(messageRepository).save(any(Message.class));
                mapperMock.verify(() -> MessageMapper.toEntity(request));
            }
        }
    }

    @Nested
    class FindTests {
        @Test
        void shouldReturnMessageResponse_whenMessageExists() {
            Message foundMessage = createTestMessage();
            MessageResponse expectedResponse = createTestResponse();

            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.of(foundMessage));

            try (MockedStatic<MessageMapper> mapperMock = Mockito.mockStatic(MessageMapper.class)) {
                mapperMock.when(() -> MessageMapper.toResponse(foundMessage)).thenReturn(expectedResponse);

                MessageResponse response = messageService.findById(TEST_ID);
                assertMessageResponse(response);
                verify(messageRepository).findById(TEST_ID);
                mapperMock.verify(() -> MessageMapper.toResponse(foundMessage));
            }
        }

        @Test
        void shouldThrowException_whenMessageNotFound() {
            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> messageService.findById(TEST_ID));
            assertEquals("No message found.", exception.getMessage());
            verify(messageRepository).findById(TEST_ID);
        }

        @Test
        void shouldReturnMessages_whenFound() {
            Message foundMessage = createTestMessage();
            MessageResponse expectedResponse = createTestResponse();

            when(messageRepository.findAll()).thenReturn(List.of(foundMessage));

            try (MockedStatic<MessageMapper> mapperMock = Mockito.mockStatic(MessageMapper.class)) {
                mapperMock.when(() -> MessageMapper.toResponse(foundMessage)).thenReturn(expectedResponse);

                List<MessageResponse> responses = messageService.findAll();

                assertNotNull(responses);
                assertEquals(1, responses.size());
                assertMessageResponse(responses.get(0));
                verify(messageRepository).findAll();
                mapperMock.verify(() -> MessageMapper.toResponse(foundMessage));
            }
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void shouldReturnUpdatedResponse_whenMessageExists() {
            Message existingMessage = createTestMessage();
            MessageRequest updateRequest = createTestRequest();
            Message updatedMessage = createTestMessage();
            MessageResponse expectedResponse = createTestResponse();

            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.of(existingMessage));
            when(messageRepository.save(any(Message.class))).thenReturn(updatedMessage);

            try (MockedStatic<MessageMapper> mapperMock = Mockito.mockStatic(MessageMapper.class)) {
                mapperMock.when(() -> MessageMapper.toResponse(updatedMessage)).thenReturn(expectedResponse);

                MessageResponse response = messageService.update(TEST_ID, updateRequest);

                assertMessageResponse(response);
                verifyUpdateOperation(updateRequest);
            }
        }

        @Test
        void shouldReturnNull_whenMessageNotFound() {
            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.empty());

            assertNull(messageService.update(TEST_ID, createTestRequest()));
            verify(messageRepository).findById(TEST_ID);
            verify(messageRepository, never()).save(any(Message.class));
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void shouldDeleteMessage_whenMessageExists() {
            Message existingMessage = createTestMessage();
            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.of(existingMessage));

            messageService.deleteById(TEST_ID);

            verify(messageRepository).findById(TEST_ID);
            verify(messageRepository).delete(existingMessage);
        }

        @Test
        void shouldThrowException_whenMessageNotFound() {
            when(messageRepository.findById(TEST_ID)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> messageService.deleteById(TEST_ID));
            assertEquals("Could not find message.", exception.getMessage());
            verify(messageRepository).findById(TEST_ID);
            verify(messageRepository, never()).delete(any(Message.class));
        }
    }

    private void assertMessageResponse(MessageResponse response) {
        assertNotNull(response);
        assertEquals(TEST_MESSAGE, response.getMessage());
        assertEquals(TEST_SENDER, response.getSender());
        assertEquals(TEST_RECEIVER, response.getReceiver());
    }

    private void verifyTimestamp(Message message) {
        assertNotNull(message.getTimestamp(), "Timestamp should not be null");
        assertTrue(message.getTimestamp().matches("\\d+"), "Timestamp should be numeric");
    }

    private void verifyUpdateOperation(MessageRequest updateRequest) {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        assertEquals(TEST_ID, capturedMessage.getId());
        assertEquals(updateRequest.getMessage(), capturedMessage.getMessage());
        assertEquals(updateRequest.getSender(), capturedMessage.getSender());
        assertEquals(updateRequest.getReceiver(), capturedMessage.getReceiver());
        assertEquals(TEST_TIMESTAMP, capturedMessage.getTimestamp());
    }
}