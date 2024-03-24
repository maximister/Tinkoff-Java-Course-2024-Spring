package edu.java.scrapper.repository.jooqRepository;

import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.repository.jooq.tables.Link.LINK;

@Repository("JooqLinkRepository")
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext context;

    public JooqLinkRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public List<Link> getLinks(List<Long> linksId) {
        return context.selectFrom(LINK)
            .where(LINK.LINK_ID.in(linksId))
            .fetchInto(Link.class);
    }

    @Override
    public Link addLink(URI url) {
        return context.insertInto(LINK, LINK.URL, LINK.LAST_CHECK, LINK.LAST_UPDATE)
            .values(url.toString(), OffsetDateTime.now(), OffsetDateTime.now())
            .returning()
            .fetchInto(Link.class)
            .getFirst();
    }

    @Override
    public Link deleteLink(long linkId) {
        return context.deleteFrom(LINK)
            .where(LINK.LINK_ID.eq(linkId))
            .returning()
            .fetchAnyInto(Link.class);
    }

    @Override
    public void updateLink(LinkInfo linkInfo) {
        context.update(LINK)
            .set(LINK.LAST_UPDATE, linkInfo.lastModified())
            .set(LINK.LAST_CHECK, OffsetDateTime.now())
            .where(LINK.URL.eq(linkInfo.url().toString()))
            .execute();
    }

    @Override
    public Link findLinkById(long linkId) {
        return context.selectFrom(LINK)
            .where(LINK.LINK_ID.eq(linkId))
            .fetchAnyInto(Link.class);
    }

    @Override
    public Link findLinkByUrl(URI url) {
        return context.selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchAnyInto(Link.class);
    }

    @Override
    public List<Link> findLinksByUpdateTime(Duration offset) {
        OffsetDateTime minimalTime = OffsetDateTime.now().minus(offset);
        return context.selectFrom(LINK)
            .where(LINK.LAST_CHECK.lessOrEqual(minimalTime))
            .fetchInto(Link.class);
    }
}
