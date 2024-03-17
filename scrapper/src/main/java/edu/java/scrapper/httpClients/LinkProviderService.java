package edu.java.scrapper.httpClients;

import jakarta.annotation.Nullable;
import java.net.URI;

public interface LinkProviderService {
    @Nullable
    LinkInfo fetch(URI url);

    boolean isValid(URI url);
}
