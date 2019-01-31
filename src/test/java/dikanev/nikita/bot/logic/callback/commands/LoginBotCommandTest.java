package dikanev.nikita.bot.logic.callback.commands;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.service.client.parameter.HttpGetParameter;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginBotCommandTest extends VkCommandTest {

    @Test
    void way1(){
        String reqText = "start";
        CommandResponse resp = new CommandResponse(1, 0, new HttpGetParameter(), getMessage(reqText), getJsonObject(reqText));
        resp.setState(new JsonObject());
        resp.setText(reqText);

        WayMenuCommand wayMenuCommand = new LoginBotCommand();
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("привет.\n" +
                "для входа в систему мне надо узнать тебя лучше.\n" +
                "ты в любой момент можешь ввести команду 'назад', чтобы вернуться к прошлому варианту.\n" +
                "для начала давай знакомиться.\n" +
                "как тебя зовут?", getLastVkMessage().toLowerCase());

        resp.setText("Никита");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите свою фамилию", getLastVkMessage().toLowerCase());

        resp.setText("Диканев");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите свой email", getLastVkMessage().toLowerCase());

        resp.setText("dikanev1998");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("некорректный email.", getLastVkMessage().toLowerCase());

        resp.setText("integration@mail.ru");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("придумайте логин", getLastVkMessage().toLowerCase());

        resp.setText("Назад");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите свой email", getLastVkMessage().toLowerCase());

        resp.setText("Назад");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите свою фамилию", getLastVkMessage().toLowerCase());

        resp.setText("Dikanev");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("введите свой email", getLastVkMessage().toLowerCase());

        resp.setText("integration.test@mail.ru");
        assertDoesNotThrow(() -> wayMenuCommand.handle(resp, resp.getArgs()));
        assertEquals("придумайте логин", getLastVkMessage().toLowerCase());

        Parameter parameter = resp.getArgs();
        assertEquals("Никита", parameter.getF("name"));
        assertEquals("Dikanev", parameter.getF("s_name"));
        assertEquals("integration.test@mail.ru", parameter.getF("email"));
    }

}