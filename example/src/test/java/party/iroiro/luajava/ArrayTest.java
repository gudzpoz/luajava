package party.iroiro.luajava;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static party.iroiro.luajava.Lua.LuaError.OK;
import static party.iroiro.luajava.Lua.LuaError.RUNTIME;

public class ArrayTest {
    @Test
    public void arrayTest() {
        try (Lua L = new Lua51()) {
            int[] i = new int[] { 1, 2, 3, 4 };
            L.pushJavaArray(i);
            L.setGlobal("i");
            assertEquals(OK, L.run("assert(i[1] == 1)"));
            assertEquals(OK, L.run("assert(i[2] == 2)"));
            assertEquals(OK, L.run("assert(i[3] == 3)"));
            assertEquals(OK, L.run("assert(i[4] == 4)"));
            assertEquals(RUNTIME, L.run("assert(i[5] == nil)"));
            assertTrue(Objects.requireNonNull(L.toString(-1))
                    .contains("java.lang.ArrayIndexOutOfBoundsException"));

            assertEquals(OK, L.run("assert(#i == 4)"));

            assertEquals(OK, L.run("i[1] = 100"));
            assertEquals(100, i[0]);
        }
    }
}
