package dikanev.nikita.bot.logic.callback.commands.menu;

import com.google.gson.Gson;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.controller.commands.CommandController;
import dikanev.nikita.bot.service.server.CallbackApiHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoMenuCommandTest {

    CallbackApiHandler callbackApiHandler = new CallbackApiHandler();

    CommandController mockCommandController = mock(CommandController.class);

    @BeforeEach
    void setUp() {

    }

    @Test
    void getPhoto() throws SQLException, ApiException, NoSuchFieldException, IllegalAccessException {
        when(mockCommandController.getCurrentCommand(147952026)).thenReturn(new HashMap<>(Map.of("id_command", 4, "args", "&message=default")));
        setOur(mockCommandController);

        String message = "{\"type\":\"message_new\",\"object\":{\"id\":4162,\"date\":1546708207,\"out\":0,\"user_id\":147952026,\"read_state\":0,\"title\":\"\",\"body\":\"Посмотреть\",\"payload\":\"{\\\"button\\\":\\\"Посмотреть\\\"}\"},\"group_id\":167918981}\n";
        callbackApiHandler.parse(message);
    }

    @Test
    void addPhoto(){

    }

    private void setOur(CommandController mockCommandController) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = CommandController.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(CommandController.getInstance(), mockCommandController);
    }
}