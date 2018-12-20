package dikanev.nikita.bot.api.objects.deserialization;

import com.google.gson.*;
import dikanev.nikita.bot.api.objects.ApiObject;
import dikanev.nikita.bot.api.objects.ApiObjects;
import dikanev.nikita.bot.api.objects.ArrayObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArrayJsonAdapter implements JsonDeserializer<ArrayObject> {

    private static final Logger LOG = LoggerFactory.getLogger(ArrayJsonAdapter.class);

    @Override
    public ArrayObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String typeObjectsArray = object.get("typeObjects").getAsString();

        try {
            Class<? extends ApiObject> clazz = ApiObjects.getObjectClass(typeObjectsArray);
            if (clazz == null) {
                //todo: сделать нормальную обработку массивов
            }
            Object arr = context.deserialize(object.get("objects"), Array.newInstance(clazz, 0).getClass());
            List<ApiObject> list = new ArrayList<>(List.of((ApiObject[]) arr));

            return new ArrayObject(typeObjectsArray, list);
        } catch (Exception e) {
            LOG.warn("Error in converting the item.", e);
            System.out.println(typeObjectsArray);
            e.printStackTrace();
            throw new IllegalStateException("Error in converting the item.");
        }
    }
}
