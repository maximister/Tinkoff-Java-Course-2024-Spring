package edu.java.scrapper.controller;

import edu.java.scrapper.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@Slf4j
public class TgChatsController {
    private final TgChatService tgChatService;

    public TgChatsController(@Qualifier("JdbcChatService") TgChatService tgChatService) {
        this.tgChatService = tgChatService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> addChat(
        @PathVariable("id")
        Long id
    ) {
        log.info("TgChatsController received POST request from user {}", id);
        return tgChatService.addChat(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(
        @PathVariable
        Long id
    ) {
        log.info("TgChatsController received DELETE request from user {}", id);
        return tgChatService.deleteChat(id);
    }
}
