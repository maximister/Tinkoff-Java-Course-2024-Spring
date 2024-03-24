package edu.java.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.comand.CommandHandler;
import edu.java.bot.processor.AbstractChainProcessor;
import edu.java.bot.processor.Processor;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.sender.BotMessageSender;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LinkTrackerBot implements UpdatesListener, AutoCloseable, ExceptionHandler {
    private final TelegramBot telegramBot;
    private final BotMessageSender sender;
    private final Processor processor;
    private final CommandMenuBuilder commandMenuBuilder;

    public LinkTrackerBot(
        CommandHandler handler,
        TelegramBot telegramBot,
        BotMessageSender sender,
        CommandMenuBuilder commandMenuBuilder
    ) {
        this.telegramBot = telegramBot;
        this.processor = AbstractChainProcessor.makeChain(
            new UserMessageProcessor(handler)
        );
        this.sender = sender;
        this.commandMenuBuilder = commandMenuBuilder;
    }

    <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        telegramBot.execute(request);
    }

    @PostConstruct
    public void start() {
        telegramBot.setUpdatesListener(this, this);

        //Создание меню с коммандами
        telegramBot.execute(commandMenuBuilder.buildCommandMenu());

        log.info("Bot is ready to work");
    }

    @Override
    public int process(List<Update> updates) {
        try {
            for (Update update : updates) {
                SendMessage answer = processor.process(update);
                sender.sendMessage(answer);
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        } catch (Exception e) {
            log.error("Error in bot {}", e.toString());
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }

    }

    @Override
    public void close() throws Exception {
        telegramBot.shutdown();
        log.info("Bot was closed");
    }

    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            log.error(
                "Telegram error: {} - {}",
                e.response().errorCode(),
                e.response().description()
            );
        } else {
            log.error(e.toString());
        }
    }
}
