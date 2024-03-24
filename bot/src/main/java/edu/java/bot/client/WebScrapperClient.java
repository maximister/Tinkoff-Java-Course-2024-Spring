package edu.java.bot.client;

import edu.java.bot.exceptions.ClientException;
import edu.java.bot.exceptions.ServerException;
import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.ApiErrorResponse;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerException)
            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientException)
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(
                Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_DURATION)
                    .filter(throwable -> throwable instanceof ServerException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())

            )
            .doOnError(this::logResponseError)
            .block();
    }

    @Override
    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        return webClient
            .post()
            .uri(LINK_ENDPOINT)
            .header(TG_CHAT_HEADER, chatId.toString())
            .bodyValue(addLinkRequest)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerException)
            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientException)
            .bodyToMono(LinkResponse.class)
            .retryWhen(
                Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_DURATION)
                    .filter(throwable -> throwable instanceof ServerException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())

            )
            .doOnError(this::logResponseError)
            .block();
    }

    @Override
    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        return webClient
            .method(HttpMethod.DELETE)
            .uri(LINK_ENDPOINT)
            .header(TG_CHAT_HEADER, chatId.toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .bodyValue(removeLinkRequest)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerException)
            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientException)
            .bodyToMono(LinkResponse.class)
            .retryWhen(
                Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_DURATION)
                    .filter(throwable -> throwable instanceof ServerException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())

            )
            .doOnError(this::logResponseError)
            .block();
    }

    @Override
    public void addChat(Long id) {
        webClient
            .post()
            .uri(CHAT_ENDPOINT + "/" + id.toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerException)
            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientException)
            .bodyToMono(void.class)
            .retryWhen(
                Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_DURATION)
                    .filter(throwable -> throwable instanceof ServerException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())

            )
            .doOnError(this::logResponseError)
            .block();
    }

    @Override
    public void deleteChat(Long id) {
        webClient.delete()
            .uri(CHAT_ENDPOINT + "/" + id.toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerException)
            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientException)
            .bodyToMono(void.class)
            .retryWhen(
                Retry.backoff(RETRY_MAX_ATTEMPTS, RETRY_DURATION)
                    .filter(throwable -> throwable instanceof ServerException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())

            )
            .doOnError(this::logResponseError)
            .block();
    }

    private Mono<? extends Throwable> handleClientException(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(ApiErrorResponse.class)
            .flatMap(
                apiErrorResponse -> Mono.error(
                    new ClientException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description(),
                        HttpStatus.valueOf(Integer.parseInt(apiErrorResponse.code()))
                    )
                )
            );
    }

    private Mono<? extends Throwable> handleServerException(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(ApiErrorResponse.class)
            .flatMap(
                apiErrorResponse -> Mono.error(
                    new ServerException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description(),
                        HttpStatus.valueOf(Integer.parseInt(apiErrorResponse.code()))
                    )
                )
            );
    }

    private void logResponseError(Throwable error) {
        log.debug("An error has occurred {}", error.getMessage());
    }
}
