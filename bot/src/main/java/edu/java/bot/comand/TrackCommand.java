package edu.java.bot.comand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exceptions.BotException;
import edu.java.bot.model.ParsedCommand;
import edu.java.bot.model.scrapperClientDto.AddLinkRequest;
import edu.java.bot.service.commandService.CommandService;
import edu.java.bot.utils.CommandUtils;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public final class TrackCommand extends AbstractCommand {
    private final CommandService service;

    private final static String COMMAND_NAME = "track";
    private final static String DESCRIPTION = "Start tracking link. Example of using: /track <link>";
    private static final String INVALID_LINK_MESSAGE = "Your link is invalid. Please try again";
    private static final String CORRECT_LINK_MESSAGE = "Started tracking your link";

    public TrackCommand(CommandService service) {
        super(COMMAND_NAME, DESCRIPTION);
        this.service = service;
    }

    @Override
    public SendMessage handle(Update update) {
        logMessage(update);
        Long chatId = update.message().chat().id();
        String userMessage = update.message().text();

        ParsedCommand parsedCommand = CommandUtils.parseCommand(userMessage);
        if (parsedCommand.arguments().isEmpty()
            || !CommandUtils.isLinkValid(parsedCommand.arguments().getFirst())) {
            return new SendMessage(chatId, INVALID_LINK_MESSAGE);
        }

        try {
            service.addLink(
                chatId,
                new AddLinkRequest(URI.create(parsedCommand.arguments().getFirst()))
            );
        } catch (BotException e) {
            return handleBotException(chatId, e);
        }

        return new SendMessage(chatId, CORRECT_LINK_MESSAGE);
    }
}
