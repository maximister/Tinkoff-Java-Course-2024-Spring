package edu.java.scrapper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.scrapper.controller.LinksController;
import edu.java.scrapper.exceptions.LinkAlreadyTrackedException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksController.class)
public class LinksControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean(name = "JdbcLinkService")
    private LinkService service;
    @Autowired
    private ObjectMapper mapper;
    private final long linkId = 1111;
    private static final URI URL = URI.create("yrlvdplsh.com");
    private static RemoveLinkRequest removeLinkRequest;
    private static AddLinkRequest addLinkRequest;

    @BeforeAll public static void start() {
        removeLinkRequest = new RemoveLinkRequest(URL);
        addLinkRequest = new AddLinkRequest(URL);
    }

    //Стоит ли писать столько тестов для контроллера или это перебор?

    @SneakyThrows
    @Test
    @DisplayName("correct get request")
    public void getLinks_shouldReturnOk() {
        Mockito.when(service.getLinks(1L))
            .thenReturn(new ResponseEntity<>(
                new ListLinksResponse(List.of(new LinkResponse(linkId, URL)), 1),
                HttpStatus.OK
            ));

        mvc.perform(get("/links").header("Tg-Chat-Id", 1L).contentType("application/json")).andExpect(status().isOk())
            .andExpect(jsonPath("$.links[0].id").value(linkId))
            .andExpect(jsonPath("$.links[0].url").value(URL.toString()));
    }

    @Test
    @DisplayName("no header get request")
    @SneakyThrows
    public void getLinks_shouldReturnBadRequest_whenHeaderIsMissed() {
        mvc.perform(get("/links").contentType("application/json")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Wrong header get request")
    @SneakyThrows
    public void getLinks_shouldReturnBadRequest_whenHeaderIsWrong() {
        mvc.perform(get("/links").contentType("application/json").header("Tg-Chat-Id", "asd"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Correct delete request")
    public void deleteLink_shouldReturnOk() {

        Mockito.when(service.deleteLink(1L, removeLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.OK));

        mvc.perform(delete("/links").header("Tg-Chat-Id", 1L).contentType("application/json")
                .content(mapper.writeValueAsString(removeLinkRequest))).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(linkId)).andExpect(jsonPath("$.url").value(URL.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("No header delete request")
    public void deleteLink_shouldReturnBadRequest_whenNoHeader() {

        Mockito.when(service.deleteLink(1L, removeLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.BAD_REQUEST));

        mvc.perform(delete("/links").contentType("application/json")
            .content(mapper.writeValueAsString(removeLinkRequest))).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Wrong header delete request")
    public void deleteLink_shouldReturnBadRequest_whenHeaderIsWrong() {
        Mockito.when(service.deleteLink(1L, removeLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.BAD_REQUEST));

        mvc.perform(delete("/links").header("Tg-Chat-Id", "a").contentType("application/json")
            .content(mapper.writeValueAsString(removeLinkRequest))).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("No body delete request")
    public void deleteLink_shouldReturnBadRequest_whenNoBody() {
        Mockito.when(service.deleteLink(1L, removeLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.BAD_REQUEST));
        mvc.perform(
            delete("/links")
                .header("Tg-Chat-Id", 1L)
                .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Wrong body delete request")
    public void deleteLink_shouldReturnBadRequest_whenBodyIsWrong() {

        Mockito.when(service.deleteLink(1L, removeLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.BAD_REQUEST));

        mvc.perform(
                delete("/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Correct post request")
    public void addLink_shouldReturnOk() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenReturn(new ResponseEntity<>(new LinkResponse(linkId, URL), HttpStatus.OK));

        mvc.perform(
                post("/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(mapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(linkId))
            .andExpect(jsonPath("$.url").value(URL.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Unsupported link post request")
    public void addLink_shouldReturnBadRequest_whenLinkUnsupported() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new UnsupportedLinkException(URL));

        mvc.perform(
                post("/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(mapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Already tracked link post request")
    public void addLink_shouldReturnConflict_whenLinkIsAlreadyTracked() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new LinkAlreadyTrackedException(URL));
        mvc.perform(
                post("/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(mapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    @DisplayName("No header post request")
    public void addLink_shouldReturnBadRequest_whenNoHeader() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new LinkAlreadyTrackedException(URL));

        mvc.perform(
                post("/links")
                    .contentType("application/json")
                    .content(mapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Wrong header post request")
    public void addLink_shouldReturnBadRequest_whenHeaderIsWrong() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new UnsupportedLinkException(URL));

        mvc.perform(
                post("/links")
                    .contentType("application/json")
                    .header("Tg-Chat-Id", "")
                    .content(mapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("No body post request")
    public void addLink_shouldReturnBadRequest_whenNoBody() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new UnsupportedLinkException(URL));

        mvc.perform(
                post("/links")
                    .contentType("application/json")
                    .header("Tg-Chat-Id", 1L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Wrong body post request")
    public void addLink_shouldReturnBadRequest_whenBodyIsWrong() {
        Mockito.when(service.addLink(1L, addLinkRequest))
            .thenThrow(new UnsupportedLinkException(URL));

        mvc.perform(
                post("/links")
                    .contentType("application/json")
                    .header("Tg-Chat-Id", 1L)
                    .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
