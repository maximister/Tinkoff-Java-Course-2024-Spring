package edu.java.bot.comand;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {
    String command();

    String description();

    SendMessage handle(Update update);

    default boolean supports(Update update) {
        return update.message() != null
            && update.message().text() != null;
        //по идее эту функцию на себя теперь берет нововведенная мапа в commandHandler,
        //а взаимодействие с коммандами идет только через этот класс
            //&& update.message().text().startsWith(command());
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
