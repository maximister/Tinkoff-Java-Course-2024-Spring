package edu.java.scrapper.httpClients.botClient;

import edu.java.scrapper.model.botClientDto.LinkUpdate;

public interface BotClient {
    void sendMessage(LinkUpdate linkUpdateRequest);
}
