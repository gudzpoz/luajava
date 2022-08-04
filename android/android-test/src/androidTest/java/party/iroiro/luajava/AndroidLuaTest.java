package party.iroiro.luajava;

import org.junit.Test;

public class AndroidLuaTest {
    @Test
    public void lua51Test() {
        new LuaTestSuite<>(new Lua51(), Lua51::new).test();
    }
    @Test
    public void lua52Test() {
    }
    @Test
    public void lua53Test() {
    }
    @Test
    public void lua54Test() {
    }
    @Test
    public void luaJitTest() {
    }
}
