package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.commands.VkCommandTest;
import dikanev.nikita.bot.logic.callback.commands.WayMenuCommand;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMenuCommandTest extends VkCommandTest {
    @Test
    void way1(){
        String reqText = "Игры";
        CommandResponse resp = new CommandResponse(147952026, 10, new HttpGetParameter(), getMessage(reqText), getJsonObject(reqText));
        resp.setText(reqText);
        resp.setState(new JsonObject());

        WayMenuCommand wayMenuCommand = new GameMenuCommand();
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("help");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("игры - просмотр доступных игр.\n" +
                "мои - просмотр игр на которых вы уже записаны.\n" +
                "записаться - запись на игру.\n" +
                "help - информация по командам\n" +
                "menu - возврщение в главное меню.", getLastVkMessage().toLowerCase());

        resp.setText("Игры");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("пока недоступно.", getLastVkMessage().toLowerCase());
    }
}