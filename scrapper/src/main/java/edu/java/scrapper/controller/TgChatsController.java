package edu.java.scrapper.controller;

import edu.java.scrapper.model.ControllerDto.ApiErrorResponse;
import edu.java.scrapper.service.TgChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409",
                     description = "Чат уже зарегистрирован",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> addChat(
        @Parameter(description = "Id чата в telegram", required = true)
        @PathVariable("id")
        Long id
    ) {
        log.info("TgChatsController received POST request from user {}", id);
        return tgChatService.addChat(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteChat(
        @Parameter(description = "Id чата в telegram", required = true)
        @PathVariable
        Long id
    ) {
        log.info("TgChatsController received DELETE request from user {}", id);
        return tgChatService.deleteChat(id);
    }
}
