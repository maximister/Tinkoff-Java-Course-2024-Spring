package edu.java.scrapper.repository.jooqRepository;

import edu.java.scrapper.model.domainDto.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.repository.jooq.tables.Chat.CHAT;


@Repository("JooqChatRepository")
public class JooqChatRepository implements ChatRepository {
    private final DSLContext context;

    public JooqChatRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public void addChat(long chatId) {
        context.insertInto(CHAT, CHAT.CHAT_ID)
            .values(chatId)
            .execute();
    }

    @Override
    public void deleteChat(long chatId) {
        context.deleteFrom(CHAT)
            .where(CHAT.CHAT_ID.eq(chatId))
            .execute();
    }

    @Override
    public List<Chat> getAllChats() {
        return context.selectFrom(CHAT)
            .fetchInto(edu.java.scrapper.model.domainDto.Chat.class);
    }

    @Override
    public edu.java.scrapper.model.domainDto.Chat findChatById(long chatId) {
        return context.selectFrom(CHAT)
            .where(CHAT.CHAT_ID.eq(chatId))
            .fetchInto(edu.java.scrapper.model.domainDto.Chat.class)
            .getFirst();
    }
}
