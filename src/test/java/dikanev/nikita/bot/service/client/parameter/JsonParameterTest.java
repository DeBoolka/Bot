package dikanev.nikita.bot.service.client.parameter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParameterTest {
    static Parameter parameter;

    @BeforeAll
    static void before() {
        parameter = new JsonParameter("{\n" +
                "  \"k1\": \"v1\",\n" +
                "  \"k2\": \"v2\",\n" +
                "  \"k3\": [\"3\", \"v3\"],\n" +
                "  \"k4\": [\"4\", \"4\"]\n" +
                "}");
    }

    @Test
    void getParameter() {
    }

    @Test
    void getContent() {
    }

    @Test
    void get() {
        assertEquals(parameter.get("k1"), List.of("v1"));
        assertEquals(parameter.get("k2"), List.of("v2"));
        assertEquals(parameter.get("k3"), List.of("3", "v3"));
        assertEquals(parameter.get("k4"), List.of("4", "4"));

        assertNull(parameter.get("k5"));
    }

    @Test
    void getOrDefault() {
        assertEquals(parameter.getOrDefault("k1", new ArrayList<>(List.of("fail"))), List.of("v1"));
        assertEquals(parameter.getOrDefault("k2", new ArrayList<>(List.of("fail"))), List.of("v2"));
        assertEquals(parameter.getOrDefault("k3", new ArrayList<>(List.of("fail"))), List.of("3", "v3"));
        assertEquals(parameter.getOrDefault("k4", new ArrayList<>(List.of("fail"))), List.of("4", "4"));

        assertEquals(parameter.getOrDefault("k5", new ArrayList<>(List.of("fail"))), List.of("fail"));
    }

    @Test
    void getF() {
        assertEquals(parameter.getF( "k1"), "v1");
        assertEquals(parameter.getF( "k2"), "v2");
        assertEquals(parameter.getF( "k3"), "3");
        assertEquals(parameter.getF( "k4"), "4");

        assertNull(parameter.getF("k5"));
    }

    @Test
    void getFOrDefault() {
        assertEquals(parameter.getFOrDefault( "k1", "fail"), "v1");
        assertEquals(parameter.getFOrDefault( "k2", "fail"), "v2");
        assertEquals(parameter.getFOrDefault( "k3", "fail"), "3");
        assertEquals(parameter.getFOrDefault( "k4", "fail"), "4");

        assertEquals(parameter.getFOrDefault( "k5", "fail"), "fail");
    }

    @Test
    void getInt() {
        assertThrows(NumberFormatException.class, () -> parameter.getInt("k1"));
        assertThrows(NumberFormatException.class, () -> parameter.getInt("k2"));
        assertThrows(NumberFormatException.class, () -> parameter.getInt("k3"));

        assertDoesNotThrow(() -> parameter.getInt("k4"));
        assertDoesNotThrow(() -> parameter.getInt("k5"));

        assertEquals(parameter.getInt("k4"), List.of(4, 4));
        assertNull(parameter.getInt("k5"));
    }

    @Test
    void getIntOrDefault() {
        assertThrows(NumberFormatException.class, () -> parameter.getIntOrDefault("k1", List.of(-1)));
        assertThrows(NumberFormatException.class, () -> parameter.getIntOrDefault("k2", List.of(-1)));
        assertThrows(NumberFormatException.class, () -> parameter.getIntOrDefault("k3", List.of(-1)));

        assertEquals(parameter.getIntOrDefault( "k4", List.of(-1)), List.of(4, 4));
        assertEquals(parameter.getIntOrDefault( "k5", List.of(-1)), List.of(-1));
    }

    @Test
    void getIntF() {
        try {
            assertThrows(NumberFormatException.class, () -> parameter.getIntF("k1"));
            assertThrows(NumberFormatException.class, () -> parameter.getIntF("k2"));

            assertDoesNotThrow(() -> parameter.getIntF("k3"));

            assertEquals(parameter.getIntF("k3"), 3);
            assertEquals(parameter.getIntF("k4"), 4);

            assertThrows(NoSuchFieldException.class, () -> parameter.getIntF("k5"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getIntFOrDefault() {
        assertThrows(NumberFormatException.class, () -> parameter.getIntFOrDefault("k1", -1));
        assertThrows(NumberFormatException.class, () -> parameter.getIntFOrDefault("k2", -1));

        assertDoesNotThrow(() -> parameter.getIntFOrDefault("k3", -1), parameter.getContent());

        assertEquals(parameter.getIntFOrDefault("k3", -1), 3);
        assertEquals(parameter.getIntFOrDefault("k4", -1), 4);

        assertEquals(parameter.getIntFOrDefault("k5", -1), -1);
    }

    @Test
    void set() {
        assertDoesNotThrow(() ->parameter.set("s1", List.of("1")));
        assertEquals(parameter.get("s1"), List.of("1"));

        assertDoesNotThrow(() ->parameter.set("s1", List.of("2")));
        assertEquals(parameter.get("s1"), List.of("2"));

        assertDoesNotThrow(() -> parameter.remove("s1"));
    }

    @Test
    void set1() {
        assertDoesNotThrow(() ->parameter.set("s1", "1"));
        assertEquals(parameter.get("s1"), List.of("1"));

        assertDoesNotThrow(() ->parameter.set("s1", "2"));
        assertEquals(parameter.get("s1"), List.of("2"));

        assertDoesNotThrow(() -> parameter.remove("s1"));
    }

    @Test
    void add() {
        assertDoesNotThrow(() ->parameter.add("s1", List.of("1")));
        assertEquals(parameter.get("s1"), List.of("1"));

        assertDoesNotThrow(() ->parameter.add("s1", List.of("2")));
        assertEquals(parameter.get("s1"), List.of("1", "2"));

        assertDoesNotThrow(() -> parameter.remove("s1"));
    }

    @Test
    void add1() {
        assertDoesNotThrow(() ->parameter.add("s1", "1"));
        assertEquals(parameter.get("s1"), List.of("1"));

        assertDoesNotThrow(() ->parameter.add("s1", "2"));
        assertEquals(parameter.get("s1"), List.of("1", "2"));

        assertDoesNotThrow(() -> parameter.remove("s1"));
    }

    @Test
    void contains() {
        assertTrue(parameter.contains("k1"));
        assertTrue(parameter.contains("k2"));
        assertTrue(parameter.contains("k3"));
        assertTrue(parameter.contains("k4"));

        assertFalse(parameter.contains("k5"));
    }

    @Test
    void containsAll() {
        assertTrue(parameter.containsAll(List.of("k1")));
        assertTrue(parameter.containsAll(List.of("k1", "k2")));
        assertTrue(parameter.containsAll(List.of("k1", "k2", "k3")));
        assertTrue(parameter.containsAll(List.of("k1", "k2", "k3", "k4")));

        assertFalse(parameter.containsAll(List.of("k1", "k2", "k3", "k5")));
        assertFalse(parameter.containsAll(List.of("k5")));
    }

    @Test
    void containsAllVal() {
        assertTrue(parameter.containsAllVal("k1", List.of("v1")));
        assertTrue(parameter.containsAllVal("k2", List.of("v2")));
        assertTrue(parameter.containsAllVal("k3", List.of("3", "v3")));
        assertTrue(parameter.containsAllVal("k4", List.of("4")));
        assertTrue(parameter.containsAllVal("k4", List.of("4", "4")));
        assertTrue(parameter.containsAllVal("k4", List.of("4", "4", "4")));

        assertFalse(parameter.containsAllVal("k5", List.of("v1", "k2", "k3", "k5")));
    }

    @Test
    void containsVal() {
        assertTrue(parameter.containsVal("k1", "v1"));
        assertTrue(parameter.containsVal("k2", "v2"));
        assertTrue(parameter.containsVal("k3", "3"));
        assertTrue(parameter.containsVal("k3", "v3", "3"));
        assertTrue(parameter.containsVal("k3", "3", "v3"));
        assertTrue(parameter.containsVal("k3", "3", "v5"));
        assertFalse(parameter.containsVal("k3", "5", "v5"));
        assertTrue(parameter.containsVal("k4", "4"));

        assertFalse(parameter.containsVal("k1", "4"));
        assertFalse(parameter.containsVal("k2", "4"));
        assertFalse(parameter.containsVal("k3", "4"));
        assertFalse(parameter.containsVal("k4", "5"));
        assertFalse(parameter.containsVal("k5", "4"));
    }

    @Test
    void containsVal1() {
        assertTrue(parameter.containsVal("k3", 3));
        assertTrue(parameter.containsVal("k4", 4));


        assertFalse(parameter.containsVal("k1", 5));
        assertFalse(parameter.containsVal("k2", 5));
        assertFalse(parameter.containsVal("k3", 5));
        assertFalse(parameter.containsVal("k4", 5));
        assertFalse(parameter.containsVal("k5", 5));
    }

    @Test
    void remove() {
        assertDoesNotThrow(() -> parameter.remove("k1"));
        assertFalse(parameter.contains("k1"));
        parameter.set("k1", "v1");

        assertDoesNotThrow(() -> parameter.remove("k1", 0));
        assertFalse(parameter.contains("k1"), parameter.getContent());
        parameter.set("k1", "v1");

        parameter.add("k3", "v32");
        assertDoesNotThrow(() -> parameter.remove("k3", 1));
        assertEquals(parameter.get("k3"), List.of("3", "v32"));
        parameter.set("k3", new ArrayList<>(List.of("3", "v3")));

        parameter.add("k3", "v32");
        assertDoesNotThrow(() -> parameter.remove("k3", "v3"));
        assertEquals(parameter.get("k3"), List.of("3", "v32"));
        parameter.set("k3", new ArrayList<>(List.of("3", "v3")));
    }

    @Test
    void isEmpty() {
        assertFalse(parameter.isEmpty());
        assertTrue(new JsonParameter().isEmpty());
    }

}