package edu.java.scrapper.model.ControllerDto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(
    @NotNull
    URI url
) {
}
