package edu.java.scrapper.model.ControllerDto;

import edu.java.scrapper.validation.ScrapperUrl;
import java.net.URI;

public record AddLinkRequest(
    @ScrapperUrl
    URI url
) {
}
