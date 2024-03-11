package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.httpClients.botClient.WebBotClient;
import edu.java.scrapper.model.botClientDto.LinkUpdate;
import java.net.URI;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class BotClientTest {
    private static final String UPDATES_ENDPOINT = "/updates";
    private static WebBotClient client;
    private static WireMockServer server;
    private static LinkUpdate dto;

    @SneakyThrows
    @BeforeAll
    public static void start() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.stubFor(WireMock.post(urlPathMatching(UPDATES_ENDPOINT))
            .willReturn(aResponse()
                .withStatus(200)));

        dto = new LinkUpdate(
            1L,
            new URI("https://google.com"),
            "test",
            Collections.emptyList()
        );

        server.start();
        client = new WebBotClient(server.baseUrl());
    }

    //честно не особо пон как тестить с учетом того, что никакой логики в контроллерах пока нет
    //работает и работает
    //Или стоит прописать конкретные жсонки с корректными и некорректными данными
    //и прописать на каждую жсонку свой код wireMock'a?

    @Test
    @DisplayName("Testing bot client post request")
    public void botClient_ShouldSendRequestToClientServer() {
        client.sendMessage(dto);
        server.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo(UPDATES_ENDPOINT)));
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }
}
