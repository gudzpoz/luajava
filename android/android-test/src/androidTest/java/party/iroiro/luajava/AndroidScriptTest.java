package party.iroiro.luajava;

import org.junit.Test;

public class AndroidScriptTest {
    @Test
    public void lua51ScriptTest() {
        new LuaScriptSuite<>(new Lua51()).test();
    }
    @Test
    public void lua52ScriptTest() {
        new LuaScriptSuite<>(new Lua52()).test();
    }
    @Test
    public void lua53ScriptTest() {
        new LuaScriptSuite<>(new Lua53()).test();
    }
    @Test
    public void lua54ScriptTest() {
        new LuaScriptSuite<>(new Lua54()).test();
    }
    @Test
    public void luaJitScriptTest() {
        // new LuaScriptSuite<>(new LuaJit()).test();
    }
}
