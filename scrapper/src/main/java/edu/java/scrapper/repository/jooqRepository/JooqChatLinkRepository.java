package edu.java.scrapper.repository.jooqRepository;

import edu.java.scrapper.repository.ChatLinkRepository;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import static edu.java.scrapper.repository.jooq.tables.ChatLink.CHAT_LINK;

@Service
public class JooqChatLinkRepository implements ChatLinkRepository {
    private final DSLContext context;

    public JooqChatLinkRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public List<Long> getLinkIdsByChatId(long chatId) {
        return context.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetchInto(Long.class);
    }

    @Override
    public List<Long> getChatIdsByLinkId(long linkId) {
        return context.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }

    @Override
    public boolean isLinkTracked(long linkId) {
        return context.fetchCount(CHAT_LINK, CHAT_LINK.LINK_ID.eq(linkId)) > 0;
    }

    @Override
    public void addChatLinkConnection(long chatId, long linkId) {
        context.insertInto(CHAT_LINK)
            .values(chatId, linkId)
            .execute();
    }

    @Override
    public void deleteChatLinkConnection(long chatId, long linkId) {
        context.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId).and(CHAT_LINK.LINK_ID.eq(linkId)))
            .execute();
    }
}
