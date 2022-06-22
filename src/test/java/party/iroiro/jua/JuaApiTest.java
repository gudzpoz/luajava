package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.jua.JuaJitNatives.lua_newuserdata;

public class JuaApiTest {
    @Test
    public void juaApiGetClassesTest() {
        Class<?>[] classes = JuaAPI.getClasses("java.lang.String,java.lang.Object," +
                "java.lang.Boolean,party.iroiro.jua.Jua");
        assertEquals(String.class, classes[0]);
        assertEquals(Object.class, classes[1]);
        assertEquals(Boolean.class, classes[2]);
        assertEquals(Jua.class, classes[3]);

        classes = JuaAPI.getClasses("java.lang.NonexistentClassProbably");
        assertNull(classes[0]);
    }

    @Test
    public void juaApiConvertFromLuaTest() {
        try (Jua L = new Jua()) {
            convertBooleanTest(L);
            convertNumberTest(L);
            convertTableTest(L);
            convertUserdataTest();
        }
    }

    private void convertTableTest(Jua L) {
        L.run("t = {a = 1; b = 2; [1] = 3; [2] = 4}");
        L.getglobal("t");

        assertEquals(Arrays.asList(3.0, 4.0), JuaAPI.convertFromLua(L, List.class, -1));

        Object map = JuaAPI.convertFromLua(L, Map.class, -1);
        assertInstanceOf(Map.class, map);
        //noinspection unchecked
        Map<Object, Object> m = (Map<Object, Object>) map;
        assertNotNull(m);
        assertEquals(1.0, m.get("a"));
        assertEquals(2.0, m.get("b"));
        assertEquals(3.0, m.get(1.0));
        assertEquals(4.0, m.get(2.0));

        Object arr = JuaAPI.convertFromLua(L, Object[].class, -1);
        assertNotNull(arr);
        assertArrayEquals(new Object[] {3.0, 4.0}, (Object[]) arr);

        // TODO: Maybe, as a feature, we can convert this for the user?
        assertThrows(IllegalArgumentException.class,
                () -> JuaAPI.convertFromLua(L, Double[].class, -1),
                "Unable to convert to " + Double[].class.getName());

        L.pop(1);
    }

    private void convertUserdataTest() {
        var L = new Jua() {
            void pushUserdata() {
                //noinspection ResultOfMethodCallIgnored
                lua_newuserdata(L, 1024);
            }
        };
        L.pushUserdata();
        assertThrows(IllegalArgumentException.class,
                () -> JuaAPI.convertFromLua(L, Integer.class, -1),
                "Unable to convert to " + Integer.class.getName());
        L.pop(1);

        Consumer<Integer> randomObject = integer -> {};
        L.push(randomObject);
        assertThrows(IllegalArgumentException.class,
                () -> JuaAPI.convertFromLua(L, Integer.class, -1),
                "Unable to convert to " + Integer.class.getName());
        assertEquals(randomObject, JuaAPI.convertFromLua(L, Consumer.class, -1));
        L.pop(1);

        L.close();
    }

    private void convertNumberTest(Jua L) {
        double n = 3.1415926;
        L.push(n);

        {
            assertEquals(true, JuaAPI.convertFromLua(L, boolean.class, -1));
            L.push(0);
            assertEquals(false, JuaAPI.convertFromLua(L, boolean.class, -1));
            L.pop(1);
        }
        assertEquals((char) n, JuaAPI.convertFromLua(L, char.class, -1));
        assertEquals((byte) n, JuaAPI.convertFromLua(L, byte.class, -1));
        assertEquals((short) n, JuaAPI.convertFromLua(L, short.class, -1));
        assertEquals((int) n, JuaAPI.convertFromLua(L, int.class, -1));
        assertEquals((long) n, JuaAPI.convertFromLua(L, long.class, -1));
        {
            Object o = JuaAPI.convertFromLua(L, float.class, -1);
            assertInstanceOf(Float.class, o);
            assertNotNull(o);
            assertEquals((float) n, (float) o, 0.000001);
        }
        assertEquals(n, JuaAPI.convertFromLua(L, double.class, -1));

        assertEquals((byte) n, JuaAPI.convertFromLua(L, Byte.class, -1));
        assertEquals((short) n, JuaAPI.convertFromLua(L, Short.class, -1));
        assertEquals((int) n, JuaAPI.convertFromLua(L, Integer.class, -1));
        assertEquals((long) n, JuaAPI.convertFromLua(L, Long.class, -1));
        Object f = JuaAPI.convertFromLua(L, Float.class, -1);
        assertNotNull(f);
        assertEquals((float) n, (float) f, 0.000001);
        assertEquals(n, JuaAPI.convertFromLua(L, Double.class, -1));
        assertThrows(IllegalArgumentException.class,
                () -> JuaAPI.convertFromLua(L, BigDecimal.class, -1),
                "Unsupported conversion");
        L.pop(1);
    }

    private void convertBooleanTest(Jua L) {
        L.push(false);
        assertEquals(false, JuaAPI.convertFromLua(L, boolean.class, -1));
        assertEquals(Boolean.FALSE, JuaAPI.convertFromLua(L, Boolean.class, -1));
        assertThrows(IllegalArgumentException.class,
                () -> JuaAPI.convertFromLua(L, Integer.class, -1),
                "Unable to convert to java.lang.Interger");
        L.pop(1);
    }
}
