package com.ycyw.back.controllers;

import com.ycyw.back.models.Message;
import com.ycyw.back.services.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void shouldSubscribeToSseStream() throws Exception {
        UUID sessionId = UUID.randomUUID();

        mockMvc.perform(get("/api/chat/stream/" + sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE));
    }

    @Test
    void shouldSendMessage() throws Exception {
        Message mockMessage = new Message();
        mockMessage.setContent("Message test");

        when(messageService.saveMessage(any(), any(), any())).thenReturn(mockMessage);

        String jsonPayload = """
            {
                "sessionId": "123e4567-e89b-12d3-a456-426614174000",
                "senderId": "123e4567-e89b-12d3-a456-426614174001",
                "content": "Message test"
            }
            """;

        mockMvc.perform(post("/api/chat/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk());
    }
}
