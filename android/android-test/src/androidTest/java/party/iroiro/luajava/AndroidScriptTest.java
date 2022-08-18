package party.iroiro.luajava;

import android.util.Log;
import org.junit.Test;

public class AndroidScriptTest {
    @Test
    public void lua51Test() {
        try (Lua51 L = new Lua51()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @Test
    public void lua52Test() {
        try (Lua52 L = new Lua52()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @Test
    public void lua53Test() {
        try (Lua53 L = new Lua53()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @Test
    public void lua54Test() {
        try (Lua54 L = new Lua54()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @Test
    public void luaJitTest() {
        try (LuaJit L = new LuaJit()) {
            new LuaScriptSuite<>(L, s -> Log.i("test", s)).test();
        }
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
            L.close();
        }
    }
}
