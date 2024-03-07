package edu.java.bot.model.scrapperClientDto;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
}
