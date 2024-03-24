package edu.java.bot.service.commandService;

import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;

public interface CommandService {
    ListLinksResponse getLinks(Long chatId);

    void registerChat(Long chatId);

    LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLink(Long chatId, RemoveLinkRequest removeLinkRequest);
}
