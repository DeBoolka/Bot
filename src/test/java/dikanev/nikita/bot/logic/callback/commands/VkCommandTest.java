package dikanev.nikita.bot.logic.callback.commands;

import com.vk.api.sdk.actions.Messages;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import dikanev.nikita.bot.Application;
import dikanev.nikita.bot.service.storage.DBStorage;
import dikanev.nikita.bot.service.storage.DataStorage;
import dikanev.nikita.bot.service.storage.ServerStorage;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import dikanev.nikita.bot.service.storage.clients.VkClientStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VkCommandTest {

    protected static VkClientStorage vkClientStorage = VkClientStorage.getInstance();

    static List<String> messagesToVk = new ArrayList<>();

    @BeforeAll
    static void initAll() {
        assertDoesNotThrow(VkCommandTest::loadConfiguration);
        assertDoesNotThrow(VkCommandTest::mockMessageToVk);
    }

    @Test
    void sendMessage(){
        new VkCommand.SendMessage(1).message("Hello").saveExecute();
        assertEquals("Hello", getLastVkMessage());
    }

    private static void mockMessageToVk() throws NoSuchFieldException, IllegalAccessException, ClientException, ApiException {
        vkClientStorage = mock(VkClientStorage.getInstance().getClass());
        Class clazz = VkClientStorage.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(VkClientStorage.getInstance(), vkClientStorage);

        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        VkApiClient vkApiClient = mock(VkApiClient.class);
        Messages messages = mock(Messages.class);
        MessageSend message = new MessageSend(vk, new GroupActor(1, ""));

        when(vkClientStorage.vk()).thenReturn(vkApiClient);
        when(vkApiClient.messages()).thenReturn(messages);
        when(messages.send(any(GroupActor.class))).thenReturn(message);
    }

    private static Properties loadConfiguration() throws Exception {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(".\\src\\test\\resources\\config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        //Подгрузка информации о сервере
        DataStorage.getInstance().init(properties);

        //Подключение к DB
        DBStorage.getInstance().init(properties);

        //Старт клиента общающегося с ядром
        CoreClientStorage.init(properties);
        return properties;
    }

    protected String getLastVkMessage() {
        if (messagesToVk.isEmpty()) {
            return null;
        }
        return messagesToVk.get(messagesToVk.size() - 1).trim();
    }

    static class MessageSend extends MessagesSendQuery {

        String message;

        public MessageSend(VkApiClient client, UserActor actor) {
            super(client, actor);
        }

        public MessageSend(VkApiClient client, GroupActor actor) {
            super(client, actor);
        }

        @Override
        public MessagesSendQuery message(String value) {
            message = value;
            return super.message(value);
        }

        @Override
        public Integer execute(){
            messagesToVk.add(message);
            return 1;
        }
    }

}