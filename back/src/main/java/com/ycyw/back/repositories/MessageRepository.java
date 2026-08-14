package com.ycyw.back.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ycyw.back.models.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

}