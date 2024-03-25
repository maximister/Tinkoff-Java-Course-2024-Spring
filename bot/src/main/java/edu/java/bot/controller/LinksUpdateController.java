package edu.java.bot.controller;

import edu.java.bot.model.controllerDto.LinkUpdate;
import edu.java.bot.model.scrapperClientDto.ApiErrorResponse;
import edu.java.bot.service.linksUpdateService.LinksUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Отправить обновление")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                     description = "Ссылка не найдена",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "429",
                     description = "Слишком много запросов",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> updateLink(
        @Valid
        @RequestBody
        LinkUpdate linkUpdateRequest
    ) {
        log.info("LinkController received POST request with body {}", linkUpdateRequest);
        return linksUpdateService.updateLink(linkUpdateRequest);
    }
}
