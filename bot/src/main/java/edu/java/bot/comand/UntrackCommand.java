package edu.java.bot.comand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exceptions.BotException;
import edu.java.bot.model.ParsedCommand;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.RemoveLinkRequest;
import edu.java.bot.service.commandService.CommandService;
import edu.java.bot.utils.CommandUtils;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public final class UntrackCommand extends AbstractCommand {
    private final CommandService service;

    private final static String COMMAND_NAME = "untrack";
    private final static String DESCRIPTION = "Removes link from your track list. Example of using: /untrack <link>";
    private static final String INVALID_UNTRACK_MESSAGE = "Your link is invalid. Please try again";
    private static final String NO_SUCH_LINK_MESSAGE = "There is no such link in your list. Please try again";
    private static final String CORRECT_UNTRACK_MESSAGE = "This link was successfully deleted";

    public UntrackCommand(CommandService service) {
        super(COMMAND_NAME, DESCRIPTION);
        this.service = service;
    }

    @Override
    public SendMessage handle(Update update) {
        logMessage(update);
        long chatId = update.message().from().id();
        String userMessage = update.message().text();

        ParsedCommand parsedCommand = CommandUtils.parseCommand(userMessage);
        if (parsedCommand.arguments().isEmpty()
            || !CommandUtils.isLinkValid(parsedCommand.arguments().getFirst())) {
            return new SendMessage(update.message().chat().id(), INVALID_UNTRACK_MESSAGE);
        }

        try {
            LinkResponse response = service.removeLink(
                chatId,
                new RemoveLinkRequest(URI.create(parsedCommand.arguments().getFirst()))
            );
            if (response.url() == null) {
                return new SendMessage(chatId, NO_SUCH_LINK_MESSAGE);
            }
        } catch (BotException e) {
            return handleBotException(chatId, e);
        }


        return new SendMessage(chatId, CORRECT_UNTRACK_MESSAGE);
    }
}
