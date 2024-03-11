package edu.java.bot.client;

import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;
import java.time.Duration;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

/**
 * В задании нужно реализовать один общий клиент для скраппера, как я понял, т.е. получается, что
 * один клиент ответственен и за запросы к контроллеру ссылок и к контроллеру чатов, что выглядит
 * как небольшое смешение зон ответственности в какой-то степени, но с другой стороны, разделение клиента
 * на 2 части выглядит как излишнее усложнение, особенно с учетом того, что работает у меня всё по идее
 * в одном потоке.
 * <br>
 * Поэтому возник вопрос: по-хорошему как обычно поступают ученые мужи?
 * <br><br>
 * Так, и получается, что раньше у меня команды использовали внутри себя inkService и тд, которые должны были
 * обращаться к репозиторию, но как я понял, у бота бд не будет, поэтом уэти сервисы будут обращаться к этому
 * клиенту по идее?
 * <br>
 * Я тогда это переделаю когда клиента проверят, и менять не так много нужно будет
 * <br>
 * Плюс тесты наверное переделывать придется все для команд(
 */

@Component
@Slf4j
public final class WebScrapperClient implements ScrapperClient {
    private final WebClient webClient;
    private static final int RETRY_MAX_ATTEMPTS = 3;
    private static final Duration RETRY_DURATION = Duration.ofMillis(100);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CHAT_ENDPOINT = "/tg-chat";
    private static final String LINK_ENDPOINT = "/links";
    private static final String TG_CHAT_HEADER = "Tg-Chat-Id";

    public WebScrapperClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public WebScrapperClient() {
        this(BASE_URL);
    }

    @Override
    public ListLinksResponse getLinks(Long chatId) {
        return webClient.get()
            .uri(LINK_ENDPOINT)
            .header(TG_CHAT_HEADER, chatId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(this::logResponseError)
            .onErrorReturn(new ListLinksResponse(Collections.emptyList(), 0))
            .block();
    }

    @Override
    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        return webClient.post()
            .uri(LINK_ENDPOINT)
            .header(TG_CHAT_HEADER, chatId.toString())
            .bodyValue(addLinkRequest)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(this::logResponseError)
            .onErrorReturn(new LinkResponse(0, null))
            .block();
    }

    @Override
    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        return webClient
            .method(HttpMethod.DELETE)
            .uri(LINK_ENDPOINT)
            .header(TG_CHAT_HEADER, chatId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(removeLinkRequest)
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(this::logResponseError)
            .onErrorReturn(new LinkResponse(0, null))
            .block();
    }

    @Override
    public void addChat(Long id) {
        webClient.post()
            .uri(CHAT_ENDPOINT, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(void.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(this::logResponseError)
            .block();
    }

    @Override
    public void deleteChat(Long id) {
        webClient.delete()
            .uri(CHAT_ENDPOINT, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(void.class)
            .retryWhen(Retry.fixedDelay(RETRY_MAX_ATTEMPTS, RETRY_DURATION))
            .doOnError(this::logResponseError)
            .block();
    }

    private void logResponseError(Throwable error) {
        log.error("An error has occurred {}", error.getMessage());
    }
}
