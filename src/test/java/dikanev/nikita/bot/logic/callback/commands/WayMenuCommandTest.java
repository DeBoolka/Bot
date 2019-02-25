package dikanev.nikita.bot.logic.callback.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.objects.messages.Message;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class WayMenuCommandTest extends VkCommandTest {

    @Test
    void way1(){
        String reqText = "start";
        CommandResponse resp = new CommandResponse(1, 10, new HttpGetParameter(), getMessage(reqText), getJsonObject(reqText));
        resp.setState(new JsonObject());

        WayMenuCommand wayMenuCommand = new WayMenuCommand() {};
        assertDoesNotThrow(() -> wayMenuCommand.init(resp, resp.getArgs()));
        assertEquals("что хотите сделать?", getLastVkMessage().toLowerCase());

        resp.setText("help");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("help - информация по командам", getLastVkMessage().toLowerCase());
    }
}