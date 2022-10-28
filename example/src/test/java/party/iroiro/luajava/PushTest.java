package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PushTest {
    @Test
    public void pushTest() {
        try (Lua L = new Lua51()) {
            L.push(Collections.singleton("string"));
            Object o = L.toObject(-1);
            assertInstanceOf(Map.class, o);
            assertNotNull(o);
            //noinspection unchecked
            assertEquals("string", ((Map<Object, Object>) o).get(1.0));

            L.push(Collections.singletonMap("k", "v"));
            Object p = L.toObject(-1);
            assertInstanceOf(Map.class, p);
            assertNotNull(p);
            //noinspection unchecked
            assertEquals("v", ((Map<Object, Object>) p).get("k"));

            L.push(null, Lua.Conversion.FULL);
            assertTrue(L.isNil(-1));

            L.push(false, Lua.Conversion.NONE);
            assertTrue(L.isUserdata(-1));

            L.push((byte) 1, Lua.Conversion.SEMI);
            assertTrue(L.isNumber(-1));
            L.push((short) 1, Lua.Conversion.SEMI);
            assertTrue(L.isNumber(-1));
            //noinspection RedundantCast
            L.push((int) 1, Lua.Conversion.SEMI);
            assertTrue(L.isNumber(-1));
            L.push((long) 1, Lua.Conversion.SEMI);
            assertTrue(L.isNumber(-1));

            L.push(Collections.singletonMap("k", "v"), Lua.Conversion.SEMI);
            assertTrue(L.isUserdata(-1));

            L.push(Collections.singletonMap("k", "v"), Lua.Conversion.FULL);
            assertTrue(L.isTable(-1));
            L.push(Collections.singletonList("l"), Lua.Conversion.FULL);
            assertTrue(L.isTable(-1));
            L.push(new int[] {1}, Lua.Conversion.FULL);
            assertTrue(L.isTable(-1));

            L.setTop(0);
        }
    }
}
