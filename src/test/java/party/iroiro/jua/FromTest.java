package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FromTest {
    @Test
    public void fromTest() {
        try (Jua L = new Jua()) {
            L.push(1);
            assertNull(L.toList(-1));
            assertNull(L.toMap(-1));

            L.pushnil();
            assertNull(L.toObject(-1));

            L.push(true);
            assertEquals(true, L.toObject(-1));
            L.push(false);
            assertEquals(false, L.toObject(-1));

            Object obj = new Object();
            L.push(obj);
            assertEquals(obj, L.toObject(-1));

            assertEquals(0, L.run("a = function () print('a') end"));
            L.getglobal("a");
            assertNull(L.toObject(-1));

            assertEquals(0, L.run("b = {[a] = 'value'}"));
            L.getglobal("b");
            Object map = L.toObject(-1);
            assertInstanceOf(Map.class, map);
            //noinspection unchecked
            assertEquals(0, ((Map<Object, Object>) map).size());

            L.settop(0);
        }
    }
}
