package edu.java.scrapper.model;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
