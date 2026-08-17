package com.ycyw.back.controllers;

import com.ycyw.back.dto.ChatSessionRequest;
import com.ycyw.back.models.ChatSession;
import com.ycyw.back.models.Message;
import com.ycyw.back.repositories.ChatSessionRepository;
import com.ycyw.back.repositories.MessageRepository;
import com.ycyw.back.services.MessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    private final MessageService messageService;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public ChatController(MessageService messageService, ChatSessionRepository chatSessionRepository,
            MessageRepository messageRepository) {
        this.messageService = messageService;
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping(value = "/stream/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable UUID sessionId) {
        SseEmitter emitter = new SseEmitter(300000L);
        emitters.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> emitters.get(sessionId).remove(emitter));
        emitter.onTimeout(() -> emitters.get(sessionId).remove(emitter));
        emitter.onError((e) -> emitters.get(sessionId).remove(emitter));
        return emitter;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody MessageRequest request) {
        Message savedMessage = messageService.saveMessage(
                request.sessionId(),
                request.senderId(),
                request.content());

        List<SseEmitter> sessionEmitters = emitters.get(request.sessionId());
        if (sessionEmitters != null) {
            for (SseEmitter emitter : sessionEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(savedMessage));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ChatSession>> getSessions(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatSessionRepository.findByClientId(userId));
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createSession(@RequestBody ChatSessionRequest request) {
        ChatSession newSession = new ChatSession();
        newSession.setClientId(request.userId());
        newSession.setStatus("OPEN");
        return ResponseEntity.ok(chatSessionRepository.save(newSession));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Message>> getHistory(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(messageRepository.findBySessionIdOrderBySentAtAsc(sessionId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) {
        chatSessionRepository.deleteById(sessionId);
        return ResponseEntity.ok().build();
    }
}

record MessageRequest(UUID sessionId, UUID senderId, String content) {
}