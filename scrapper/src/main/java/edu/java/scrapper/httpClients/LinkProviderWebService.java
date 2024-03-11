package edu.java.scrapper.httpClients;

import java.net.URL;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public abstract class LinkProviderWebService implements LinkProviderService {
    protected WebClient webClient;
    protected static final int RETRY_MAX_ATTEMPTS = 3;
    protected static final Duration RETRY_DURATION = Duration.ofMillis(100);
    protected Logger log = LoggerFactory.getLogger(LinkProviderWebService.class);

    public LinkProviderWebService(String baseUrl) {
        webClient = WebClient.create(baseUrl);
    }

    protected abstract boolean isValid(URL url);

    protected <T> T doRequest(String uri, Class<T> dtoClass, T onErrorValue) {
        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .accept()
            .retrieve()
            .bodyToMono(dtoClass)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
            .onErrorReturn(onErrorValue)
            .block();
    }
}
