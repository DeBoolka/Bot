package dikanev.nikita.bot.model.storage;

import dikanev.nikita.bot.model.callback.commands.VkCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandStorage {

    private static final Logger LOG = LoggerFactory.getLogger(CommandStorage.class);

    private static CommandStorage ourInstance = new CommandStorage();

    Map<Integer, VkCommand> commandMap = new HashMap<>();

    public static CommandStorage getInstance() {
        return ourInstance;
    }
}
