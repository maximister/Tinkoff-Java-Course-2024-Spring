package edu.java.scrapper.repository.jdbcRepository;

import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * тут взоникли некоторые беды с запросами, поэтому я решил разделить зоны ответственности
 * и перенес всю работу с таблицей связи отсюда в отдельный репозиторий, а связывать их будет
 * сервис.
 * Но в связи с этим есть некоторые вопросики в плане дизайна, по которым нужно будет посоветоваться
 * они будут в сервисе ссылок навеное)
 */
@Repository("JdbcLinkRepository")
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LinkRowMapper mapper;

    private static final String ID = "link_id";
    private static final String UPDATE = "last_update";
    private static final String CHECK = "last_check";
    private static final String URL = "url";

    public JdbcLinkRepository(JdbcTemplate jdbcTemplate, LinkRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<Link> getLinks(List<Long> linksId) {
        StringBuilder placelolders = new StringBuilder(linksId.size());
        placelolders.repeat("?,", linksId.size());
        placelolders.deleteCharAt(linksId.size() * 2 - 1);

        String query = "SELECT * FROM link WHERE link_id IN(?)";
        String finalQuery = query.replace("?", placelolders.toString());

        return jdbcTemplate.query(finalQuery, mapper, linksId.toArray());
    }

    @Override
    public Link addLink(URI url) {
        String query = "INSERT INTO link (url, last_update, last_check) VALUES(?, ?, ?) RETURNING *";
        OffsetDateTime updateTime = OffsetDateTime.now();
        return jdbcTemplate.queryForObject(query, mapper, url.toString(), updateTime, updateTime);
    }

    @Override
    public Link deleteLink(long linkId) {
        String query = "DELETE FROM link WHERE link_id = (?) RETURNING *";
        return jdbcTemplate.queryForObject(query, mapper, linkId);
    }

    @Override
    public void updateLink(LinkInfo linkInfo) {
        String query = "UPDATE link SET last_update = ?, last_check = ? WHERE url = ?";
        jdbcTemplate.update(query, linkInfo.lastModified(), OffsetDateTime.now(), linkInfo.url().toString());
    }

    @Override
    public Link findLinkById(long linkId) {
        String query = "SELECT * FROM link WHERE link_id = ?";
        return jdbcTemplate.query(query, mapper, linkId).stream().findAny().orElse(null);
    }

    @Override
    public Link findLinkByUrl(URI url) {
        String query = "SELECT * FROM link WHERE url = ?";
        return jdbcTemplate.query(query, mapper, url.toString()).stream().findAny().orElse(null);
    }

    @Override
    public List<Link> findLinksByUpdateTime(Duration offset) {
        OffsetDateTime minimalTime = OffsetDateTime.now().minus(offset);
        String query = "SELECT * FROM link WHERE last_check < ?";
        return jdbcTemplate.query(query, mapper, minimalTime);
    }

    @Component
    public static class LinkRowMapper implements RowMapper<Link> {
        @SneakyThrows
        @Override
        public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Link(
                rs.getLong(ID),
                rs.getObject(UPDATE, OffsetDateTime.class),
                rs.getObject(CHECK, OffsetDateTime.class),
                new URI(rs.getObject(URL, String.class))
            );
        }
    }
}
