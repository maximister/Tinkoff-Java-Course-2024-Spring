package edu.java.bot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class BotMessageSender {
    private final TelegramBot bot;

    public BotMessageSender(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendMessage(SendMessage message) {
        bot.execute(message);
    }
}
