package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PushTest {
    @Test
    public void pushTest() {
        try (Jua L = new Jua()) {
            L.push(Collections.singleton("string"));
            Object o = L.toObject(-1);
            assertInstanceOf(Map.class, o);
            //noinspection unchecked
            assertEquals("string", ((Map<Object, Object>) o).get(1.0));

            L.push(Collections.singletonMap("k", "v"));
            Object p = L.toObject(-1);
            assertInstanceOf(Map.class, p);
            //noinspection unchecked
            assertEquals("v", ((Map<Object, Object>) p).get("k"));

            L.pushJava(null, Jua.Conversion.FULL);
            assertTrue(L.isnil(-1));

            L.pushJava(false, Jua.Conversion.NONE);
            assertTrue(L.isuserdata(-1));

            L.pushJava((byte) 1, Jua.Conversion.SEMI);
            assertTrue(L.isnumber(-1));
            L.pushJava((short) 1, Jua.Conversion.SEMI);
            assertTrue(L.isnumber(-1));
            //noinspection RedundantCast
            L.pushJava((int) 1, Jua.Conversion.SEMI);
            assertTrue(L.isnumber(-1));
            L.pushJava((long) 1, Jua.Conversion.SEMI);
            assertTrue(L.isnumber(-1));

            L.pushJava(Collections.singletonMap("k", "v"), Jua.Conversion.SEMI);
            assertTrue(L.isuserdata(-1));

            L.pushJava(Collections.singletonMap("k", "v"), Jua.Conversion.FULL);
            assertTrue(L.istable(-1));
            L.pushJava(Collections.singletonList("l"), Jua.Conversion.FULL);
            assertTrue(L.istable(-1));
            L.pushJava(new int[] {1}, Jua.Conversion.FULL);
            assertTrue(L.istable(-1));

            L.settop(0);
        }
    }
}
