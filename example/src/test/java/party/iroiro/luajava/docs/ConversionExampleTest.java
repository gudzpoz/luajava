package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua51.Lua51;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConversionExampleTest {
    @Test
    public void noAutoUnboxingTest() {
// #region noAutoUnboxingTest
try (Lua L = new Lua51()) {
    L.push(1, Lua.Conversion.NONE);
    L.setGlobal("i");
    L.run("print(i:hashCode())");
    assertThrows(LuaException.class, () -> L.run("print(i + 1)"));
    L.run("print(java.luaify(i) + 1)");
}
// #endregion noAutoUnboxingTest
    }

    @Test
    public void fullConversionTest() {
// #region fullConversionTest
try (Lua L = new Lua51()) {
    int[] array = new int[]{100};
    L.push(array, Lua.Conversion.FULL);
    L.setGlobal("array");
    L.run("array[1] = 1024");
    assert 100 == array[0];
}
// #endregion fullConversionTest
    }
}
