package party.iroiro.luajava;

import org.junit.Test;

public class AndroidLuaTest {
    @Test
    public void lua51Test() {
        try (Lua51 L = new Lua51()) {
            new LuaTestSuite<>(L, Lua51::new).test();
        }
    }

    @Test
    public void lua52Test() {
        try (Lua52 L = new Lua52()) {
            new LuaTestSuite<>(L, Lua52::new).test();
        }
    }

    @Test
    public void lua53Test() {
        try (Lua53 L = new Lua53()) {
            new LuaTestSuite<>(L, Lua53::new).test();
        }
    }

    @Test
    public void lua54Test() {
        try (Lua54 L = new Lua54()) {
            new LuaTestSuite<>(L, Lua54::new).test();
        }
    }

    @Test
    public void luaJitTest() {
        try (LuaJit L = new LuaJit()) {
            new LuaTestSuite<>(L, LuaJit::new).test();
        }
    }
}
