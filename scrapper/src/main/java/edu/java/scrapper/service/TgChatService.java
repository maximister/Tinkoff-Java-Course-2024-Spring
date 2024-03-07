package edu.java.scrapper.service;

import org.springframework.http.ResponseEntity;

public interface TgChatService {
    ResponseEntity<Void> addChat(Long id);

    ResponseEntity<Void> deleteChat(Long id);
}
