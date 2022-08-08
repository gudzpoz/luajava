package party.iroiro.luajava;

import android.util.Log;
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
        new LuaScriptSuite<>(new LuaJit(), s -> Log.i("test", s)).test();
    }

    @Test
    public void memoryTest() {
        //noinspection resource
        Lua[] Ls = new Lua[]{
                new Lua51(),
                new Lua52(),
                new Lua53(),
                new Lua54(),
                new LuaJit(),
        };
        for (Lua L : Ls) {
            LuaScriptSuite.memoryTest(L);
        }
    }
}
