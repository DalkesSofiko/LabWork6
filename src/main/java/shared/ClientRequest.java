package shared;

import java.io.Serializable;

/**
 * запрос, который клиент отправляет на сервер.
 * Реализует Serializable для передачи по сети через UDP.
 */
public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commandName;
    private Serializable argument;

    // команды без аргументов
    public ClientRequest(String commandName) {
        this.commandName = commandName;
        this.argument = null;
    }

    // команды с аргументпами
    public ClientRequest(String commandName, Serializable argument) {
        this.commandName = commandName;
        this.argument = argument;
    }

    public String getCommandName() {
        return commandName;
    }

    public Serializable getArgument() {
        return argument;
    }
}