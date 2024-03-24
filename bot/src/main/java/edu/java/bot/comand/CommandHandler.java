package edu.java.bot.comand;

import edu.java.bot.model.ParsedCommand;
import edu.java.bot.utils.CommandUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandHandler {
    private final List<Command> commands;
    private final Map<String, Command> commandsMap;
    private HelpCommand help;

    public CommandHandler(List<Command> commands) {
        this.commands = new ArrayList<>();
        this.commandsMap = new HashMap<>();

        for (Command command : commands) {
            if (!(command instanceof HelpCommand)) {
                addCommand(command);
                commandsMap.put(command.command(), command);
            } else {
                this.help = (HelpCommand) command;
                addCommand(command);
            }
        }

        if (help == null) {
            help = new HelpCommand();
            commands.forEach(c -> help.updateMessage(c));
        }

        commandsMap.put(help.command(), help);
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public void addCommand(Command command) {
        log.info("new command {} was added", command.command());
        if (!commandsMap.containsKey(command.command())) {
            commandsMap.put(command.command(), command);
            commands.add(command);
            help.updateMessage(command);
        }
    }

    public Command getCommand(String message) {
        ParsedCommand parsedCommand = CommandUtils.parseCommand(message);
        return commandsMap.get(parsedCommand.command());
    }
}
