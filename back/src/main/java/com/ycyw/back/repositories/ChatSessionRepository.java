package com.ycyw.back.repositories;

import com.ycyw.back.models.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByClientId(UUID clientId);
}
