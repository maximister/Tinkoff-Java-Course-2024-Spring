package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.comand.Command;
import edu.java.bot.comand.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMessageProcessor extends AbstractChainProcessor {
    private final CommandHandler commandHandler;

    private final static String NO_TEXT_MESSAGE =
        "please don't send anything other than text, it's hard for me to work with";
    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Unsupported command. Please use command /help to see all supported commands";

    public UserMessageProcessor(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public SendMessage process(Update update) {
        long id = update.message().chat().id();
        if (update.message().text() != null) {
            Command command = commandHandler.getCommand(update.message().text());
            if (command != null && command.supports(update)) {
                log.info("Command from {} was processed", update.message().from());
                return command.handle(update);
            } else {
                log.info("Received unsupported command");
                return new SendMessage(id, UNSUPPORTED_COMMAND_MESSAGE);
            }
        }
        log.info("Received command with no text");
        return new SendMessage(id, NO_TEXT_MESSAGE);
    }

    @Override
    public SendMessage check(Update update) {
        if (update.message() != null) {
            return process(update);
        }
        return checkNext(update);
    }
}
