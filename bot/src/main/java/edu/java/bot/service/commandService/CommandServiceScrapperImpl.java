package edu.java.bot.service.commandService;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;
import org.springframework.stereotype.Service;

@Service
public class CommandServiceScrapperImpl implements CommandService {
    private final ScrapperClient scrapperClient;

    public CommandServiceScrapperImpl(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public ListLinksResponse getLinks(Long chatId) {
        return scrapperClient.getLinks(chatId);
    }

    @Override
    public void registerChat(Long chatId) {
        scrapperClient.addChat(chatId);
    }

    @Override
    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        return scrapperClient.addLink(chatId, addLinkRequest);
    }

    @Override
    public LinkResponse removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperClient.deleteLink(chatId, removeLinkRequest);
    }
}
