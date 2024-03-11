package edu.java.scrapper.controllers;

import edu.java.scrapper.controller.TgChatsController;
import edu.java.scrapper.exceptions.ChatAlreadyRegisteredException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.UnauthorizedChatException;
import edu.java.scrapper.service.TgChatService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TgChatsController.class)
public class TgChatControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private TgChatService service;

    @SneakyThrows
    @Test
    @DisplayName("correct post request")
    public void addChat_shouldReturnOk() {
        Mockito.when(service.addChat(Mockito.anyLong()))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/tg-chat/1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("correct delete request")
    public void deleteChat_shouldReturnOk() {
        Mockito.when(service.deleteChat(Mockito.anyLong()))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(delete("/tg-chat/1")).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @DisplayName("invalid (non existing chat) delete request")
    public void deleteChat_shouldReturnNotFound() {
        Mockito.doThrow(new ChatNotFoundException(1L))
            .when(service).deleteChat(Mockito.anyLong());

        mvc.perform(delete("/tg-chat/1")).andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("invalid (chat already exist) post request")
    public void deleteChat_shouldReturnConflict() {
        Mockito.doThrow(new ChatAlreadyRegisteredException(1L))
            .when(service).addChat(Mockito.anyLong());

        mvc.perform(post("/tg-chat/1")).andExpect(status().isConflict());
    }

    @SneakyThrows
    @Test
    @DisplayName("invalid (unauthorized chat) delete request")
    public void deleteChat_shouldReturnUnauthorized() {
        Mockito.doThrow(new UnauthorizedChatException(1L))
            .when(service).deleteChat(Mockito.anyLong());

        mvc.perform(delete("/tg-chat/1")).andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @Test
    @DisplayName("invalid endpoint requests")
    public void deleteChat_shouldReturnBadRequest() {
        mvc.perform(delete("/tg-chat/1e1")).andExpect(status().isBadRequest());
        mvc.perform(post("/tg-chat/aboba")).andExpect(status().isBadRequest());
    }
}
