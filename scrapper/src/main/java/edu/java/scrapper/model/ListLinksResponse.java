package edu.java.scrapper.model;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
}
