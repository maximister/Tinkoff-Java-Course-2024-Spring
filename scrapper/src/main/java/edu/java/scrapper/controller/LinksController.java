package edu.java.scrapper.controller;

import edu.java.scrapper.model.AddLinkRequest;
import edu.java.scrapper.model.LinkResponse;
import edu.java.scrapper.model.ListLinksResponse;
import edu.java.scrapper.model.RemoveLinkRequest;
import edu.java.scrapper.service.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@Slf4j
public class LinksController {

    //по идее тут везде могу вернуть только заглушки
    //дальше как я понял, с появлением бд нужно дабить слои в виде сервиса и репозитория, да?

    private final LinkService linkService;

    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ResponseEntity<ListLinksResponse> getLinks(
        @NotNull
        @RequestHeader("Tg-Chat-Id")
        Long tgChatId
    ) {
        log.info("LinkController received GET request from user {}", tgChatId);
        return linkService.getLinks(tgChatId);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLink(
        @NotNull
        @RequestHeader("Tg-Chat-Id")
        Long tgChatId,
        @Valid
        @RequestBody
        AddLinkRequest addLinkRequest
    ) {
        log.info("LinkController received POST request from user {} with {} body",
            tgChatId, addLinkRequest
        );
        return linkService.addLink(tgChatId, addLinkRequest);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> deleteLink(
        @NotNull
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId,
        @Valid
        @RequestBody
        RemoveLinkRequest removeLinkRequest
    ) {
        log.info("LinkController received DELETE request from user {} with {} body",
            tgChatId, removeLinkRequest
        );
        return linkService.deleteLink(tgChatId, removeLinkRequest);
    }
}
