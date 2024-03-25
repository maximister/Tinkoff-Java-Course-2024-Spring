package edu.java.scrapper.httpClients.botClient;

import edu.java.scrapper.model.botClientDto.LinkUpdate;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Slf4j
public class WebBotClient implements BotClient {
    private final WebClient webClient;
    private static final int RETRY_MAX_ATTEMPTS = 3;
    private static final Duration RETRY_DURATION = Duration.ofMillis(100);
    private static final String BASE_URL = "http://localhost:8090";
    private static final String UPDATES_ENDPOINT = "/updates";

    public WebBotClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public WebBotClient() {
        this(BASE_URL);
    }

    @Override
    public void sendMessage(LinkUpdate linkUpdateRequest) {
        webClient.post()
            .uri(UPDATES_ENDPOINT)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(linkUpdateRequest)
            .retrieve()
            .bodyToMono(void.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
            .block();
    }
}
