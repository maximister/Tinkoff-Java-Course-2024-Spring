package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.comand.UntrackCommand;
import edu.java.bot.model.scrapperClientDto.LinkResponse;
import edu.java.bot.model.scrapperClientDto.ListLinksResponse;
import edu.java.bot.service.commandService.CommandService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class UntrackCommandTest {
    private static Update update;
    private static Message message;
    private static Chat chat;
    private static User user;
    private static CommandService service;

    private static final String INVALID_UNTRACK_MESSAGE = "Your link is invalid. Please try again";
    private static final String NO_SUCH_LINK_MESSAGE = "There is no such link in your list. Please try again";
    private static final String CORRECT_UNTRACK_MESSAGE = "This link was successfully deleted";

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

    @ParameterizedTest
    @CsvSource(value = {
        "yrlvdplsh",
        "ev"
    }, emptyValue = "ev")
    @DisplayName("untrack command with invalid or no link")
    public void untrackCommandTest_ShouldReturnInvalidLinkMessage(String link) {
        Mockito.when(message.text()).thenReturn("/track" + link);
        SendMessage answer = new UntrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(INVALID_UNTRACK_MESSAGE)).isTrue();
    }

    @Test
    @DisplayName("untrack command with untracked link")
    public void untrackCommandTest_ShouldReturnNoSuchLinkMessage() {
        Mockito.when(message.text()).thenReturn("/track https://ylvdplsh");
        Mockito.when(service.removeLink(Mockito.anyLong(), Mockito.any()))
            .thenReturn(new LinkResponse(0, null));
        SendMessage answer = new UntrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(NO_SUCH_LINK_MESSAGE)).isTrue();
    }

    @Test
    @DisplayName("untrack command should untrack link")
    public void untrackCommandTest_ShouldReturnCorrectUntrackMessage() {
        Mockito.when(service.getLinks(1L))
            .thenReturn(
                new ListLinksResponse(
                    List.of(new LinkResponse(1, URI.create("https://yrlvdplsh"))),
                    1
                )
            );
        Mockito.when(service.removeLink(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new LinkResponse(1L, URI.create("https://yrlvdplsh")));
        Mockito.when(message.text()).thenReturn("/track https://yrlvdplsh");

        SendMessage answer = new UntrackCommand(service).handle(update);

        assertThat(answer.getParameters().get("text").equals(CORRECT_UNTRACK_MESSAGE)).isTrue();
    }
}
