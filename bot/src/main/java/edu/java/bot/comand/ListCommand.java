package edu.java.bot.comand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exceptions.BotException;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.service.commandService.CommandService;
import org.springframework.stereotype.Component;

@Component
public final class ListCommand extends AbstractCommand {
    private final CommandService service;

    private final static String COMMAND_NAME = "list";
    private final static String DESCRIPTION = "Prints all tracked links";
    private final static String DEFAULT_MESSAGE = "Your links:\n";
    private final static String EMPTY_LIST_MESSAGE = "There are no any tracked links";

    public ListCommand(CommandService service) {
        super(COMMAND_NAME, DESCRIPTION);
        this.service = service;
    }

    @Override
    public SendMessage handle(Update update) {
        logMessage(update);
        long chatId = update.message().chat().id();
        try {
            ListLinksResponse links = service.getLinks(update.message().from().id());

            if (links.size() == 0) {
                return new SendMessage(chatId, EMPTY_LIST_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder(DEFAULT_MESSAGE);

                for (LinkResponse link : links.links()) {
                    message.append("— ")
                        .append(link.url())
                        .append('\n');
                }

                return new SendMessage(chatId, message.toString());
            }
        } catch (BotException e) {
            return handleBotException(chatId, e);
        }
    }
}
