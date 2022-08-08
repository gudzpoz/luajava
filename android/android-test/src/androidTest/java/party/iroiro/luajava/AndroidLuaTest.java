package party.iroiro.luajava;

import org.junit.Test;

public class AndroidLuaTest {
    @Test
    public void lua51Test() {
        new LuaTestSuite<>(new Lua51(), Lua51::new).test();
    }
    @Test
    public void lua52Test() {
        new LuaTestSuite<>(new Lua52(), Lua52::new).test();
    }
    @Test
    public void lua53Test() {
        new LuaTestSuite<>(new Lua53(), Lua53::new).test();
    }
    @Test
    public void lua54Test() {
        new LuaTestSuite<>(new Lua54(), Lua54::new).test();
    }
    @Test
    public void luaJitTest() {
        new LuaTestSuite<>(new LuaJit(), LuaJit::new).test();
    }
}
