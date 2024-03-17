package edu.java.scrapper.model.ControllerDto;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
}
