package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FromTest {
    @Test
    public void fromTest() {
        try (Lua L = new Lua51()) {
            L.push(1);
            assertNull(L.toList(-1));
            assertNull(L.toMap(-1));

            L.pushNil();
            assertNull(L.toObject(-1));

            L.push(true);
            assertEquals(true, L.toObject(-1));
            L.push(false);
            assertEquals(false, L.toObject(-1));

            Object obj = new Object();
            L.push(obj, Lua.Conversion.NONE);
            assertEquals(obj, L.toObject(-1));

            assertEquals(Lua.LuaError.OK, L.run("a = function () print('a') end"));
            L.getGlobal("a");
            assertNull(L.toObject(-1));

            assertEquals(Lua.LuaError.OK, L.run("b = {[a] = 'value'}"));
            L.getGlobal("b");
            Object map = L.toObject(-1);
            assertInstanceOf(Map.class, map);
            //noinspection unchecked
            assertEquals(0, ((Map<Object, Object>) map).size());

            L.pushNil();
            assertNull(L.toObject(-1, Class.class));

            FromTest from = new FromTest();
            L.push(from, Lua.Conversion.NONE);
            assertEquals(from, L.toObject(-1, FromTest.class));
            assertThrows(IllegalArgumentException.class,
                    () -> L.toObject(-1, String.class),
                    "Unable to convert type");

            double n = 3.0;
            L.push(n);
            assertThrows(IllegalArgumentException.class,
                    () -> L.toObject(-1, BigInteger.class),
                    "Unable to convert type");
            assertEquals((byte) n, L.toObject(-1, byte.class));
            assertEquals((byte) n, L.toObject(-1, Byte.class));
            assertEquals((short) n, L.toObject(-1, short.class));
            assertEquals((short) n, L.toObject(-1, Short.class));
            assertEquals((int) n, L.toObject(-1, int.class));
            assertEquals((int) n, L.toObject(-1, Integer.class));
            assertEquals((long) n, L.toObject(-1, long.class));
            assertEquals((long) n, L.toObject(-1, Long.class));
            assertEquals((float) n, L.toObject(-1, float.class));
            assertEquals((float) n, L.toObject(-1, Float.class));
            assertEquals(n, L.toObject(-1, double.class));
            assertEquals(n, L.toObject(-1, Double.class));

            L.setTop(0);
        }
    }
}
