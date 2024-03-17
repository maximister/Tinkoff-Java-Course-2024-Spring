package edu.java.scrapper.model.ControllerDto;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
