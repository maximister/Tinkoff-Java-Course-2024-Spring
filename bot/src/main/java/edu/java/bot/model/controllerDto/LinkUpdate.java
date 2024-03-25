package edu.java.bot.model.controllerDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
    @PositiveOrZero
    Long id,
    @NotNull
    URI url,
    @NotBlank
    String description,
    @NotEmpty
    List<Long> tgChatIds) {
}
