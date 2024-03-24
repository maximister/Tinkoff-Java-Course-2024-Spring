package edu.java.bot.comand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exceptions.BotException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCommand implements Command {
    private String command;
    private String description;
    private final static String PREFIX = "/";

    public AbstractCommand(String command, String description) {
        this.command = PREFIX + command;
        this.description = description;
    }

    @Override
    public String command() {
        return command;
    }

    @Override
    public String description() {
        return description;
    }

    protected void logMessage(Update update) {
        log.info(
            "Processed message from {} with text {}",
            update.message().from(),
            update.message().text()
        );
    }

    protected SendMessage handleBotException(Long chatId, BotException e) {
        return new SendMessage(
            chatId,
            e.getMessage() != null ? e.getMessage() : "Sorry, unexpected error("
        );
    }
}
