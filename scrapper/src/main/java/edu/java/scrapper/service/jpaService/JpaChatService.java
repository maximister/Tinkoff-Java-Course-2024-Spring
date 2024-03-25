package edu.java.scrapper.service.jpaService;

import edu.java.scrapper.exceptions.ChatAlreadyRegisteredException;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.entity.ChatEntity;
import edu.java.scrapper.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JpaChatService implements TgChatService {
    private final JpaChatRepository repository;

    public JpaChatService(JpaChatRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Void> addChat(Long id) {
        try {
            repository.save(new ChatEntity(id));
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
            repository.deleteById(id);
            log.info("Chat with id {} was successfully deleted", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException e) {
            log.info("Chat with id {} was not found during removing", id);
            throw new ChatAlreadyRegisteredException(id);
        }
    }
}
