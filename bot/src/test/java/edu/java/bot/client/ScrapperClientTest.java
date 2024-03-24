package edu.java.bot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.net.URI;
import java.util.List;
import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class ScrapperClientTest {
    private static final String CHAT_ENDPOINT = "/tg-chat";
    private static final String LINK_ENDPOINT = "/links";
    private static final String TG_CHAT_HEADER = "Tg-Chat-Id";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static WebScrapperClient client;
    private static WireMockServer server;

    @SneakyThrows
    @BeforeAll
    public static void start() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        //Chat controller
        //post
        server.stubFor(post(urlPathMatching(CHAT_ENDPOINT + "/1"))
            .willReturn(aResponse()
                .withStatus(200)));
        //delete
        server.stubFor(delete(urlPathMatching(CHAT_ENDPOINT + "/1"))
            .willReturn(aResponse()
                .withStatus(200)));

        //Links controller
        //get
        server.stubFor(get(urlPathMatching(LINK_ENDPOINT))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(MAPPER.writeValueAsString(
                    new ListLinksResponse(
                        List.of(new LinkResponse(1L, URI.create("aboba.com"))),
                        1
                    )))));
        //post
        server.stubFor(post(urlPathMatching(LINK_ENDPOINT))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(MAPPER.writeValueAsString(
                    (new LinkResponse(1L, URI.create("abobus.com")))
                ))));
        //delete
        server.stubFor(delete(urlPathMatching(LINK_ENDPOINT))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(MAPPER.writeValueAsString(
                    (new LinkResponse(1L, URI.create("oguzok.com")))
                ))));

        server.start();
        client = new WebScrapperClient(server.baseUrl());
    }

    @Test
    @DisplayName("post request to chat controller")
    @SneakyThrows
    public void addChat_shouldSendRequestToChatController() {
        client.addChat(1L);

        server.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo(CHAT_ENDPOINT + "/1")));
    }

    @Test
    @DisplayName("delete request to chat controller")
    @SneakyThrows
    public void deleteChat_shouldSendRequestToChatController() {
        client.deleteChat(1L);

        server.verify(1, WireMock.deleteRequestedFor(WireMock.urlEqualTo(CHAT_ENDPOINT + "/1")));
    }

    @Test
    @DisplayName("get request to link controller")
    @SneakyThrows
    public void getLinks_shouldSendRequestToLinkController() {

        ListLinksResponse response = client.getLinks(1L);
        ListLinksResponse expected = new ListLinksResponse(
            List.of(new LinkResponse(1L, URI.create("aboba.com"))),
            1
        );

        assertThat(response).isEqualTo(expected);
        server.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(LINK_ENDPOINT)));

    }

    @Test
    @DisplayName("post request to link controller")
    @SneakyThrows
    public void addLink_shouldSendRequestToLinkController() {

        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("abobus.com"));
        LinkResponse response = client.addLink(1L, addLinkRequest);

        LinkResponse expected = new LinkResponse(1L, URI.create("abobus.com"));

        assertThat(response).isEqualTo(expected);
        server.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo(LINK_ENDPOINT)));
    }

    @Test
    @DisplayName("delete request to link controller")
    @SneakyThrows
    public void deleteLink_shouldSendRequestToLinkController() {

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("oguzok.com"));
        LinkResponse response = client.deleteLink(1L, removeLinkRequest);

        LinkResponse expected = new LinkResponse(1L, URI.create("oguzok.com"));

        assertThat(response).isEqualTo(expected);
        server.verify(1, WireMock.deleteRequestedFor(WireMock.urlEqualTo(LINK_ENDPOINT)));
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }

}
