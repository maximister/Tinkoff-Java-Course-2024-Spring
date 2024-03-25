package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.comand.StartCommand;
import edu.java.bot.service.commandService.CommandService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class StartCommandTest {
    private static Update update;
    private static Message message;
    private static Chat chat;
    private static User user;
    private static CommandService service;

    private final static String DEFAULT_MESSAGE = """
        Welcome to Link Tracker Bot
        You can interact with me using commands described in the /help command
        """;
    @BeforeAll
    public static void initialize() {
        update = Mockito.mock(Update.class);
        message = Mockito.mock(Message.class);
        chat = Mockito.mock(Chat.class);
        user = Mockito.mock(User.class);
        service = Mockito.mock(CommandService.class);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(chat.id()).thenReturn(1L);
        Mockito.when(user.id()).thenReturn(1L);
    }

    @Test
    @DisplayName("Testing default start command behaviour")
    public void startCommandTest_ShouldReturnDefaultMessage() {
        SendMessage answer = new StartCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(DEFAULT_MESSAGE)).isTrue();
    }
}
