package edu.java.bot.comand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exceptions.BotException;
import edu.java.bot.service.commandService.CommandService;
import org.springframework.stereotype.Component;

@Component
public final class StartCommand extends AbstractCommand {

    private final CommandService service;

    public StartCommand(CommandService commandService) {
        super(COMMAND_NAME, DESCRIPTION);
        this.service = commandService;
    }

    private final static String COMMAND_NAME = "start";
    private final static String DESCRIPTION = "Starts bot work";
    private final static String DEFAULT_MESSAGE = """
        Welcome to Link Tracker Bot
        You can interact with me using commands described in the /help command
        """;

    @Override
    public SendMessage handle(Update update) {
        logMessage(update);
        long chatId = update.message().chat().id();
        try {
            service.registerChat(update.message().from().id());
        } catch (BotException e) {
            return handleBotException(chatId, e);
        }
        return new SendMessage(chatId, DEFAULT_MESSAGE);
    }
}
