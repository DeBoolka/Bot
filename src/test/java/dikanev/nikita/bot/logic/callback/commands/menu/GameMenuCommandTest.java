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
    void menuWay(){
        String reqText = "Игры";
        CommandResponse resp = getResp(10, reqText);

        WayMenuCommand wayMenuCommand = new GameMenuCommand();
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("help");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("игры - просмотр доступных игр.\n" +
                "мои - просмотр игр на которых вы уже записаны.\n" +
                "записаться - запись на игру.\n" +
                "help - информация по командам\n" +
                "меню - возврщение в главное меню.", getLastVkMessage().toLowerCase());
    }

    @Test
    void getAllGames() {
        String reqText = "Игры";
        CommandResponse resp = getResp(10, reqText);

        WayMenuCommand wayMenuCommand = new GameMenuCommand();
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("Игры");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("список всех игр:\n" +
                "id\tназвание\tгород\tдата", getVkMessage(1).toLowerCase());
        assertEquals("false", resp.getArgs().getF("hasNext"));

        resp.setText("Вперед");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("3\tне зона 1\tставрополь\t2019-03-08 03:00:00.0\n" +
                "1\tзона 1\tмосква\t2019-03-13 18:00:00.0", getLastVkMessage().toLowerCase());

        resp.setText("Назад");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("3\tне зона 1\tставрополь\t2019-03-08 03:00:00.0\n" +
                "1\tзона 1\tмосква\t2019-03-13 18:00:00.0", getLastVkMessage().toLowerCase());

        resp.setText("Закончить");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertTrue(resp.isInit());
    }

    @Test
    void getSignedUpGames() {
        String reqText = "Мои";
        CommandResponse resp = getResp(10, reqText);

        WayMenuCommand wayMenuCommand = new GameMenuCommand();
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("Мои");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("список игр на которые вы уже записаны:\n" +
                "id\tназвание\tстатус\tгород\tдата", getVkMessage(1).toLowerCase());
        assertEquals("false", resp.getArgs().getF("hasNext"));

        resp.setText("Вперед");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("1\tне зона 1\tdenied\tставрополь\t2019-03-08 03:00:00.0\n" +
                "1\tзона 1\tawaiting\tмосква\t2019-03-13 18:00:00.0", getLastVkMessage().toLowerCase());

        resp.setText("Назад");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("1\tне зона 1\tdenied\tставрополь\t2019-03-08 03:00:00.0\n" +
                "1\tзона 1\tawaiting\tмосква\t2019-03-13 18:00:00.0", getLastVkMessage().toLowerCase());

        resp.setText("Закончить");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertTrue(resp.isInit());
    }

    @Test
    void registerUserToGame(){
        String reqText = "Записаться";
        CommandResponse resp = getResp(10, reqText);

        WayMenuCommand wayMenuCommand = new GameMenuCommand();
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("Записаться");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите номер игры:", getLastVkMessage().toLowerCase());

        resp.setText("1");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("выберите номер роли:\n" +
                "1. одиночка - 50\n" +
                "2. долг - 20\n" +
                "4. монолит - 10", getLastVkMessage().toLowerCase());

        resp.setText("4");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("выберите номер роли:\n" +
                "1. одиночка - 50\n" +
                "2. долг - 20\n" +
                "4. монолит - 10", getLastVkMessage().toLowerCase());
    }
}