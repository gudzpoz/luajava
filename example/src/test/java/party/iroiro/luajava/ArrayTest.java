package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

public class ArrayTest {
    @Test
    public void arrayTest() {
        try (Lua L = new Lua51()) {
            int[] i = new int[]{1, 2, 3, 4};
            L.pushJavaArray(i);
            L.setGlobal("i");
            L.run("assert(i[1] == 1)");
            L.run("assert(i[2] == 2)");
            L.run("assert(i[3] == 3)");
            L.run("assert(i[4] == 4)");
            assertThrowsLua(L, "assert(i[5] == nil)",
                    LuaException.LuaError.RUNTIME, ArrayIndexOutOfBoundsException.class);

            L.run("assert(#i == 4)");

            L.run("i[1] = 100");
            assertEquals(100, i[0]);
        }
    }
}
