package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.comand.Command;
import edu.java.bot.comand.CommandHandler;
import edu.java.bot.comand.HelpCommand;
import edu.java.bot.comand.ListCommand;
import edu.java.bot.comand.StartCommand;
import edu.java.bot.comand.TrackCommand;
import edu.java.bot.comand.UntrackCommand;
import edu.java.bot.service.linkService.LinkService;
import edu.java.bot.service.userService.UserService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMessageProcessorTest {
    private static Update update;
    private static Message message;
    private static UserMessageProcessor ump;

    private static Chat chat;
    private static User user;

    private static LinkService linkService;
    private static UserService userService;
    private static List<Command> commands;

    private final static String NO_TEXT_MESSAGE =
        "please don't send anything other than text, it's hard for me to work with";
    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Unsupported command. Please use command /help to see all supported commands";

    @BeforeAll
    public static void initialize() {
        update = Mockito.mock(Update.class);
        message = Mockito.mock(Message.class);
        chat = Mockito.mock(Chat.class);
        user = Mockito.mock(User.class);
        userService = Mockito.mock(UserService.class);
        linkService = Mockito.mock(LinkService.class);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.from()).thenReturn(user);
        Mockito.when(chat.id()).thenReturn(1L);
        Mockito.when(user.id()).thenReturn(1L);

        commands = List.of(
            new HelpCommand(),
            new StartCommand(userService),
            new ListCommand(linkService),
            new TrackCommand(linkService),
            new UntrackCommand(linkService)
        );


        //если создавать в каждом тесте снова, то в тесте ниже в /help будут повторы, так что пока так
        ump = new UserMessageProcessor(new CommandHandler(commands));
    }

    @ParameterizedTest
    @MethodSource("getCommandAndMessage")
    @DisplayName("test that user message processor will choose correct command to handle update")
    public void userMessageProcessor_shouldCorrectlyProcessUpdate(String command, String text) {
        Mockito.when(message.text()).thenReturn(command);

        SendMessage answer = ump.process(update);

        System.out.println(answer.getParameters().get("text"));
        assertThat(answer.getParameters().get("text").equals(text)).isTrue();
    }

    @Test
    @DisplayName("test message processor with no text message")
    public void userMessageProcessor_shouldReturnNoTextMessage() {
        Mockito.when(message.text()).thenReturn(null);

        SendMessage answer = ump.process(update);

        assertThat(answer.getParameters().get("text").equals(NO_TEXT_MESSAGE)).isTrue();
    }

    @Test
    @DisplayName("test message processor with no supportedCommand")
    public void userMessageProcessor_shouldReturnUnsupportedCommandMessage() {
        Mockito.when(message.text()).thenReturn("/tcl");

        SendMessage answer = ump.process(update);

        assertThat(answer.getParameters().get("text").equals(UNSUPPORTED_COMMAND_MESSAGE)).isTrue();
    }



        public static Stream<Arguments> getCommandAndMessage() {
        String helpMessage = """
            Welcome to Link Tracker Bot
            You can interact with me using following commands:
            /help — Prints help message with all allowed commands
            /start — Starts bot work
            /list — Prints all tracked links
            /track — Start tracking link. Example of using: /track <link>
            /untrack — Removes link from your track list. Example of using: /untrack <link>
            """;
        String startMessage = """
            Welcome to Link Tracker Bot
            You can interact with me using commands described in the /help command
            """;
        return Stream.of(
            Arguments.of("/help", helpMessage),
            Arguments.of("/start", startMessage),
            Arguments.of("/list", "There are no any tracked links"),
            Arguments.of("/track", "Your link is invalid. Please try again"),
            Arguments.of("/untrack", "Your link is invalid. Please try again")
        );
    }
}
