package edu.java.bot.model.scrapperClientDto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(
    @NotNull
    URI link
) {
}
