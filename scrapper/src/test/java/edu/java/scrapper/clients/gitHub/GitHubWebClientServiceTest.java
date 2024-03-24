package edu.java.scrapper.clients.gitHub;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.httpClients.LinkProviderService;
import edu.java.scrapper.httpClients.gitHub.GitHubWebClientService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class GitHubWebClientServiceTest {
    private static final String CORRECT_SERVER_REPO_LINK = "/yrlvdplsh/TinkoffFakeAppForScam";
    private static final String CORRECT_LINK = "https://github.com/yrlvdplsh/TinkoffFakeAppForScam";
    private static final String NON_EXISTING_REPO_LINK = "https://github.com/yrlvdplsh/stillChill";
    private static final String NOT_GITHUB_LINK = "https://andreyZamay.com";

    private static LinkProviderService service;
    private static WireMockServer server;
    private static LinkInfo CORRECT_DTO;

    @SneakyThrows
    @BeforeAll
    public static void start() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.stubFor(WireMock.get(urlPathMatching(CORRECT_SERVER_REPO_LINK))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                       "id": 111222333,
                       "name": "TinkoffFakeAppForScam",
                       "full_name": "yrlvdplsh/TinkoffFakeAppForScam",
                       "private": false,
                       "description": "best scam app",
                       "updated_at": "2023-10-02T19:07:33Z"
                     }"""
                )));
        server.stubFor(WireMock.get(urlPathMatching("/yrlvdplsh/stillChill"))
            .willReturn(aResponse()
                .withStatus(404)));

        CORRECT_DTO = new LinkInfo(
            new URI("https://github.com/yrlvdplsh/TinkoffFakeAppForScam"),
            "yrlvdplsh/TinkoffFakeAppForScam",
            "best scam app",
            OffsetDateTime.parse("2023-10-02T19:07:33Z", DateTimeFormatter.ISO_DATE_TIME)
        );

        server.start();
        service = new GitHubWebClientService(server.baseUrl());
    }

    @SneakyThrows
    @Test
    @DisplayName("testing client work with correct link")
    public void fetch_shouldReturnCorrectDto() {
        LinkInfo result = service.fetch(new URI(CORRECT_LINK));

        System.out.println(result);
        assertThat(result).isEqualTo(CORRECT_DTO);
    }

    @SneakyThrows
    @Test
    @DisplayName("testing client work with non existing repo link")
    public void fetch_shouldReturnCorrectNull_whenRepoDoesNotExist() {
        LinkInfo result = service.fetch(new URI(NON_EXISTING_REPO_LINK));

        assertThat(result).isNull();
    }

    @SneakyThrows
    @Test
    @DisplayName("testing client work with not a github link")
    public void fetch_shouldReturnCorrectNull_whenLinkIsNotRepo() {
        LinkInfo result = service.fetch(new URI(NOT_GITHUB_LINK));

        assertThat(result).isNull();
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }
}
