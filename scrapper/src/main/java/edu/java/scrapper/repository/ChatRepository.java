package edu.java.scrapper.repository;

import edu.java.scrapper.model.domainDto.Chat;
import java.util.List;

public interface ChatRepository {
    void addChat(long chatId);

    void deleteChat(long chatId);

    List<Chat> getAllChats();

    Chat findChatById(long chatId);
}
