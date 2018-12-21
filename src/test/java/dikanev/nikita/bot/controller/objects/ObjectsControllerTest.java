package dikanev.nikita.bot.controller.objects;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.exceptions.NotFoundException;
import dikanev.nikita.bot.api.objects.JObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectsControllerTest {

    @Test
    void ifExceptionThrow() {
        String body = "{\n" +
                "  \"type\": \"error\",\n" +
                "  \"code\" : 404,\n" +
                "  \"description\" : \"Desc\",\n" +
                "  \"message\" : \"new message\"\n" +
                "}";
        JObject jObject = new JObject(body);
        JObject finalJObject1 = jObject;
        assertThrows(NotFoundException.class, () -> ObjectsController.ifExceptionThrow(finalJObject1));

        body = "{\n" +
                "  \"type\": \"error\",\n" +
                "  \"code\" : 405,\n" +
                "  \"description\" : \"Desc\",\n" +
                "  \"message\" : \"new message\"\n" +
                "}";
        jObject = new JObject(body);
        JObject finalJObject = jObject;
        assertThrows(ApiException.class, () -> ObjectsController.ifExceptionThrow(finalJObject));

        body = "{\"typeObjects\":\"int\",\"objects\":[1, 2, 3],\"type\":\"array\"}";
        jObject = new JObject(body);
        JObject finalJObject2 = jObject;
        assertDoesNotThrow(() -> ObjectsController.ifExceptionThrow(finalJObject2));

    }
}