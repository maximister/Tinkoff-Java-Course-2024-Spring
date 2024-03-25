package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.comand.TrackCommand;
import edu.java.bot.service.commandService.CommandService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class TrackCommandTest {
    private static Update update;
    private static Message message;
    private static Chat chat;
    private static User user;
    private static CommandService service;


    private static final String  INVALID_LINK_MESSAGE = "Your link is invalid. Please try again";
    private static final String  CORRECT_LINK_MESSAGE = "Started tracking your link";

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
    }

    @Test
    @DisplayName("Track command with no link")
    public void trackCommandTest_noLink_ShouldReturnInvalidLinkMessage() {
        Mockito.when(message.text()).thenReturn("/track");
        SendMessage answer = new TrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(INVALID_LINK_MESSAGE)).isTrue();
    }

    @Test
    @DisplayName("Track command with invalid link")
    public void trackCommandTest_invalidLink_ShouldReturnInvalidLinkMessage() {
        Mockito.when(message.text()).thenReturn("/track ylvdplsh");
        SendMessage answer = new TrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(INVALID_LINK_MESSAGE)).isTrue();
    }

    @Test
    @DisplayName("correct track command")
    public void trackCommandTest_ShouldReturnCorrectLinkMessage() {
        Mockito.when(message.text()).thenReturn("/track https://yrlvdplsh");
        SendMessage answer = new TrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(CORRECT_LINK_MESSAGE)).isTrue();
    }
}
