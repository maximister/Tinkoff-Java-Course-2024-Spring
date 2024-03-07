package edu.java.bot.controller;

import edu.java.bot.model.controllerDto.LinkUpdate;
import edu.java.bot.service.linksUpdateService.LinksUpdateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@Slf4j
public class LinksUpdateController {

    private final LinksUpdateService linksUpdateService;

    public LinksUpdateController(LinksUpdateService linksUpdateService) {
        this.linksUpdateService = linksUpdateService;
    }

    @PostMapping
    public ResponseEntity<Void> updateLink(
        @Valid
        @RequestBody
        LinkUpdate linkUpdateRequest
    ) {
        log.info("LinkController received POST request with body {}", linkUpdateRequest);
        return linksUpdateService.updateLink(linkUpdateRequest);
    }
}
