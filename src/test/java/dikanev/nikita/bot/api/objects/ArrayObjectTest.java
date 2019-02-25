package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ArrayObjectTest {

    @Test
    void toArray() {
        String body = "{\"typeObjects\":\"accessGroup\",\"objects\":[{\"idGroup\":2,\"command\":\"group/create\",\"access\":true,\"type\":\"accessGroup\"},{\"idGroup\":2,\"command\":\"group/delete\",\"access\":true,\"type\":\"accessGroup\"},{\"idGroup\":2,\"command\":\"group/access\",\"access\":false,\"type\":\"accessGroup\"}],\"type\":\"array\"}";
        assertDoesNotThrow(() -> new JObject(body).cast(ArrayObject.empty()));

        ArrayObject arrObj = new JObject(body).cast(ArrayObject.empty());
        List<AccessCommand> lst = new ArrayList<>();
        arrObj.toList().forEach(it ->{
            JsonObject root = it.getAsJsonObject();
            int id = root.get("idGroup").getAsInt();
            String name = root.get("command").getAsString();
            boolean access = root.get("access").getAsBoolean();
            lst.add(new AccessCommand(id, name, access));
        });
        List<AccessCommand> exLst = new ArrayList<>();
        exLst.add(new AccessCommand(2, "group/create", true));
        exLst.add(new AccessCommand(2, "group/delete", true));
        exLst.add(new AccessCommand(2, "group/access", false));

        assertEquals(lst, exLst);
    }

    @Test
    void toArray1() {
        String body = "{\"typeObjects\":\"int\",\"objects\":[1, 2, 3],\"type\":\"array\"}";
        assertDoesNotThrow(() -> new JObject(body).cast(ArrayObject.empty()));

        ArrayObject arrObj = new JObject(body).cast(ArrayObject.empty());
        assertEquals(arrObj.toList(Integer.class), List.of(1, 2, 3));
    }

    private static class AccessCommand{
        int id;
        String name;
        boolean access;

        public AccessCommand(int id, String name, boolean access) {
            this.id = id;
            this.name = name;
            this.access = access;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessCommand that = (AccessCommand) o;
            return id == that.id &&
                    access == that.access &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, access);
        }

        @Override
        public String toString() {
            return id + " " + name + " " + (access ? "true" : false);
        }
    }
}