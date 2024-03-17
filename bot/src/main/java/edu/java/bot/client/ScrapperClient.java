package edu.java.bot.client;

import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;

public interface ScrapperClient {
    ListLinksResponse getLinks(Long chatId);

    LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest);

    LinkResponse deleteLink(Long chatId, RemoveLinkRequest removeLinkRequest);

    void addChat(Long id);

    void deleteChat(Long id);
}
