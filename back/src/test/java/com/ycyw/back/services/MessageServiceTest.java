package com.ycyw.back.services;

import com.ycyw.back.models.Message;
import com.ycyw.back.repositories.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void shouldSaveAndReturnMessage() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String content = "Message test";
        Message mockMessage = new Message();
        mockMessage.setId(UUID.randomUUID());
        mockMessage.setSessionId(sessionId);
        mockMessage.setSenderId(senderId);
        mockMessage.setContent(content);
        mockMessage.setSentAt(LocalDateTime.now());
        when(messageRepository.save(any(Message.class))).thenReturn(mockMessage);

        // Act
        Message savedMessage = messageService.saveMessage(sessionId, senderId, content);

        // Assert
        assertNotNull(savedMessage.getId());
        assertEquals(content, savedMessage.getContent());
        assertEquals(sessionId, savedMessage.getSessionId());
    }
}
