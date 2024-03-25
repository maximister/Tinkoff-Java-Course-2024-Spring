package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.model.controllerDto.LinkUpdate;
import edu.java.bot.service.linksUpdateService.LinksUpdateService;
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
import java.net.URI;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksUpdateController.class)
public class LinksUpdateControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private LinksUpdateService service;
    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Correct post request")
    @SneakyThrows
    public void updateLink_shouldReturnOk() {
        LinkUpdate linkUpdate =
            new LinkUpdate(3L, URI.create("test.com"), "test", List.of(1L));

        Mockito.when(service.updateLink(linkUpdate))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(
                post("/updates").
                    contentType("application/json")
                    .content(mapper.writeValueAsString(linkUpdate)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("No body post request")
    @SneakyThrows
    public void updateLink_shouldReturnBadRequest_whenNoBody() {
        LinkUpdate linkUpdate =
            new LinkUpdate(3L, URI.create("test.com"), "test", List.of(1L));
        Mockito.when(service.updateLink(linkUpdate))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(
                post("/updates")
                    .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Wrong body post request")
    @SneakyThrows
    public void updateLink_shouldReturnBadRequest_whenBodyIsWrong() {
        LinkUpdate linkUpdate =
            new LinkUpdate(3L, URI.create("test.com"), "test", List.of(1L));

        Mockito.when(service.updateLink(linkUpdate))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(
                post("/updates")
                    .contentType("application/json")
                    .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
