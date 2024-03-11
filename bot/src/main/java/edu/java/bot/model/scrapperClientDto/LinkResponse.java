package edu.java.bot.model.scrapperClientDto;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
