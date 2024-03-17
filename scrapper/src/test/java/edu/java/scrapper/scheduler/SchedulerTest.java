package edu.java.scrapper.scheduler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.httpClients.LinkProviderService;
import edu.java.scrapper.httpClients.botClient.BotClient;
import edu.java.scrapper.httpClients.botClient.WebBotClient;
import edu.java.scrapper.httpClients.gitHub.GitHubWebClientService;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.ChatLinkRepository;
import edu.java.scrapper.repository.ChatRepository;
import edu.java.scrapper.repository.LinkRepository;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Что-то я как ни пытался, все равно не получилось сделать так,
 * чтобы спринг внедрил тестовые зависимости вместо моих, а из-за этого запросы
 * идут не туда куда надо итд(
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Disabled
public class SchedulerTest extends IntegrationEnvironment {
    private static WireMockServer botServer;
    private static WireMockServer gitHubServer;

    private static BotClient botClient;
    private static LinkProviderService gitHubClient;

    private static final String BOT_ENDPOINT = "/updates";
    private static final String GIT_ENDPOINT = "/yrlvdplsh/TinkoffFakeAppForScam";
    private static final String CORRECT_LINK = "https://github.com/yrlvdplsh/TinkoffFakeAppForScam";

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private ChatLinkRepository chatLinkRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SneakyThrows
    @BeforeAll
    public static void start() {
        botServer = new WireMockServer(wireMockConfig().dynamicPort());
        botServer.stubFor(WireMock.post(urlPathMatching(BOT_ENDPOINT))
            .willReturn(aResponse()
                .withStatus(200)));

        botServer.start();
        botClient = new WebBotClient(botServer.baseUrl());

        gitHubServer = new WireMockServer(wireMockConfig().dynamicPort());
        gitHubServer.stubFor(WireMock.get(urlPathMatching(GIT_ENDPOINT))
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
                       "updated_at": "2024-04-04T19:07:33Z"
                     }"""
                )));

        gitHubServer.start();
        gitHubClient = new GitHubWebClientService(gitHubServer.baseUrl());
    }

    @SneakyThrows
    @BeforeEach
    public void fillTable() {
        chatRepository.addChat(1);
        chatRepository.addChat(2);
        chatRepository.addChat(3);

        linkRepository.addLink(new URI(CORRECT_LINK));

        chatLinkRepository.addChatLinkConnection(1, 1);
        chatLinkRepository.addChatLinkConnection(2, 1);
        chatLinkRepository.addChatLinkConnection(3, 1);
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("TRUNCATE TABLE chat_link, link, chat RESTART IDENTITY CASCADE");
    }

    @SneakyThrows
    @Test
    @DisplayName("testing scheduler work")
    public void update_shouldUpdateFirstLink() {
        LinkUpdaterScheduler scheduler =
            new LinkUpdaterScheduler(botClient, List.of(gitHubClient), linkRepository, chatLinkRepository);

        Link updatedLink = linkRepository.findLinkByUrl(URI.create(CORRECT_LINK));

        System.out.println(updatedLink.lastUpdate());
        botServer.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo(BOT_ENDPOINT)));
        gitHubServer.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(GIT_ENDPOINT)));
        assertEquals(
            updatedLink.lastUpdate(),
            OffsetDateTime.parse("2023-10-02T19:07:33Z", DateTimeFormatter.ISO_DATE_TIME)
        );
        assertEquals(
            updatedLink.lastCheck().getNano(),
            OffsetDateTime.now().getNano(),
            1_000_000
        );
    }

    @AfterAll
    public static void shutdown() {
        botServer.stop();
        gitHubServer.stop();
    }

}
