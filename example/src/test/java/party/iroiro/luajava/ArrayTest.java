package party.iroiro.luajava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.Lua.LuaError.OK;

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
            assertEquals(OK, L.run("assert(i[5] == nil)"));

            assertEquals(OK, L.run("assert(#i == 4)"));

            assertEquals(OK, L.run("i[1] = 100"));
            assertEquals(100, i[0]);
        }
    }
}
