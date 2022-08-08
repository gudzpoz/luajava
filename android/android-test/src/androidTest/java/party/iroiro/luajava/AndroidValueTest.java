package party.iroiro.luajava;

import org.junit.Test;
import party.iroiro.luajava.value.LuaValueSuite;

public class AndroidValueTest {
    @Test
    public void lua51Test() {
        new LuaValueSuite<>(new Lua51()).test();
    }
    @Test
    public void lua52Test() {
        new LuaValueSuite<>(new Lua52()).test();
    }
    @Test
    public void lua53Test() {
        new LuaValueSuite<>(new Lua53()).test();
    }
    @Test
    public void lua54Test() {
        new LuaValueSuite<>(new Lua54()).test();
    }
    @Test
    public void luaJitTest() {
        new LuaValueSuite<>(new LuaJit()).test();
    }
}
