package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.jdbcRepository.JdbcLinkRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbcLinkRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private JdbcLinkRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private OffsetDateTime time;
    private static  URI firstUrl;
    private static  URI secondUrl;
    private static  URI thirdUrl;

    public static final long DELTA = 10000000000L; //10 sec


    @SneakyThrows
    @BeforeAll
    public static void setupUri() {
        firstUrl = new URI("testLink1.com");
        secondUrl = new URI("testLink2.com");
        thirdUrl = new URI("testLink3.com");
    }

    @BeforeEach
    public void setTime() {
        time = OffsetDateTime.now();
    }

    //что-то у меня не получается с rollback и transaction,
    //вешал и на класс, и на методы, отката все равно нет, поэтому так пока(
    //буду рад советам
    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("TRUNCATE TABLE chat_link, link, chat RESTART IDENTITY CASCADE");
    }


    @SneakyThrows
    @Test
    @DisplayName("Testing add link method")
    public void addLink_shouldWorkCorrectly() {
        Link result = repository.addLink(firstUrl);

        Link expected = new Link(
            1,
            time,
            time,
            firstUrl
        );

        //comparing dto
        assertAll(
            () -> assertThat(result.linkId()).isEqualTo(expected.linkId()),
            () -> assertThat(result.url()).isEqualTo(expected.url()),
            () -> assertEquals(
                result.lastUpdate().getNano(),
                expected.lastUpdate().getNano(),
                DELTA
            ),
            () -> assertEquals(
                result.lastCheck().getNano(),
                expected.lastCheck().getNano(),
                DELTA
            )
        );

        //looking at table
        assertThat(repository.getLinks(List.of(1L))).isEqualTo(List.of(result));
    }

    @SneakyThrows
    @Test
    @DisplayName("Testing get links method")
    public void getLinks_shouldWorkCorrectly() {
        fillTableWithTestLinks();
        List<Long> requiredLinkIds = List.of(1L, 2L, 3L);

        List<Link> result = repository.getLinks(requiredLinkIds);

        List<Link> expected = List.of(
            new Link(1L, time, time, firstUrl),
            new Link(2L, time, time, secondUrl),
            new Link(3L, time, time, thirdUrl)
        );

        assertAll(
            () -> assertEquals(result.get(0).url(), expected.get(0).url()),
            () -> assertEquals(result.get(1).url(), expected.get(1).url()),
            () -> assertEquals(result.get(2).url(), expected.get(2).url()),

            () -> assertEquals(result.get(0).linkId(), expected.get(0).linkId()),
            () -> assertEquals(result.get(1).linkId(), expected.get(1).linkId()),
            () -> assertEquals(result.get(2).linkId(), expected.get(2).linkId())
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Testing delete links method")
    public void deleteLinks_shouldWorkCorrectly() {
        fillTableWithTestLinks();
        Link result = repository.deleteLink(2);

        Link expected = new Link(2L, time, time, secondUrl);

        assertAll(
            () -> assertEquals(result.linkId(), expected.linkId()),
            () -> assertEquals(result.url(), expected.url()),
            () -> assertNull(repository.findLinkById(2))
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Testing update link method")
    public void updateLink_shouldWorkCorrectly() {
        fillTableWithTestLinks();
        OffsetDateTime newTime = time.plusHours(1);
        repository.updateLink(new LinkInfo(firstUrl, 1, "", "", newTime));

        Link expected = new Link(1, newTime, newTime, firstUrl);
        Link result = repository.findLinkById(1);

        assertAll(
            () -> assertEquals(expected.url(), result.url()),
            () -> assertEquals(expected.linkId(), result.linkId()),
            () -> assertEquals(expected.lastUpdate().getNano(), result.lastUpdate().getNano(), DELTA),
            () -> assertEquals(expected.lastCheck().getNano(), OffsetDateTime.now().getNano(), DELTA)
        );
    }

    @Test
    @DisplayName("Testing find link by url method")
    public void findLinkByUrl_shouldWorkCorrectly() {
        fillTableWithTestLinks();

        Link result = repository.findLinkByUrl(firstUrl);
        assertEquals(result.linkId(), 1L);
    }

    @Test
    @DisplayName("Testing find links by update time method")
    public void findLinksByUpdateTime_shouldWorkCorrectly() {
        fillTableWithTestLinks();

        List<Link> linksForUpdate = repository.findLinksByUpdateTime(Duration.ZERO);
        List<Link> expected = List.of(
            repository.findLinkByUrl(firstUrl),
            repository.findLinkByUrl(secondUrl),
            repository.findLinkByUrl(thirdUrl)
        );
        assertEquals(linksForUpdate, expected);
        assertEquals(repository.findLinksByUpdateTime(Duration.ofDays(1)), Collections.emptyList());
    }


    @SneakyThrows
    private void fillTableWithTestLinks() {
        repository.addLink(firstUrl);
        repository.addLink(secondUrl);
        repository.addLink(thirdUrl);
    }
}
