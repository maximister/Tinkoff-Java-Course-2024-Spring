package edu.java.scrapper.service.jooqService;

import edu.java.scrapper.exceptions.ChatAlreadyRegisteredException;
import edu.java.scrapper.repository.ChatRepository;
import edu.java.scrapper.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JooqChatService implements TgChatService {
    /**
     * Честно, пока не понял зачем по заданию отдельные сервисы для жука,
     * если его функционал полностью аналогичен ждбц и можно обойтись внедрением другого репозитория
     * Но, возможно, на моменте с жпа смысл появится, пока выглядит, как кривое условие,
     * так что тут чистый копипаст
     */

    private final ChatRepository chatRepository;

    public JooqChatService(ChatRepository repository) {
        this.chatRepository = repository;
    }

    @Override
    public ResponseEntity<Void> addChat(Long id) {
        try {
            chatRepository.addChat(id);
            log.info("Chat with id {} was successfully registered", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException e) {
            log.debug("Chat with id {} has already registered", id);
            throw new ChatAlreadyRegisteredException(id);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteChat(Long id) {
        try {
            chatRepository.deleteChat(id);
            log.info("Chat with id {} was successfully deleted", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException e) {
            log.info("Chat with id {} was not found during removing", id);
            throw new ChatAlreadyRegisteredException(id);
        }
    }
}
