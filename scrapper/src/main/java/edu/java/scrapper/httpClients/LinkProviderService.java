package edu.java.scrapper.httpClients;

import java.net.URL;

public interface LinkProviderService {
    LinkInfo fetch(URL url);
}
