package edu.java.scrapper.controller;

import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.ApiErrorResponse;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import edu.java.scrapper.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final LinkService linkService;

    public LinksController(@Qualifier("JdbcLinkService") LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылки успешно получены",
                     content = @Content(schema = @Schema(implementation = ListLinksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Пользователь не зарегистрирован",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Повторное добавление ссылки",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ListLinksResponse> getLinks(
        @NotNull
        @PositiveOrZero
        @Parameter(description = "Id чата в telegram", required = true)
        @RequestHeader("Tg-Chat-Id")
        Long tgChatId
    ) {
        log.info("LinkController received GET request from user {}", tgChatId);
        return linkService.getLinks(tgChatId);
    }

    @PostMapping
    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена",
                     content = @Content(schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "202", description = "Ожидание ссылки",
                     content = @Content(schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Пользователь не зарегистрирован",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ссылка на добавление не ожидалась",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<LinkResponse> addLink(
        @NotNull
        @Parameter(description = "Id чата в telegram", required = true)
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
    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана",
                     content = @Content(schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "202", description = "Ожидание ссылки",
                     content = @Content(schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Пользователь не зарегистрирован",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ссылка на удаление не ожидалась",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<LinkResponse> deleteLink(
        @NotNull
        @Parameter(description = "Id чата в telegram", required = true)
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
