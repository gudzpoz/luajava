package party.iroiro.luajava;

import org.junit.Test;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luajit.LuaJit;

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
            new LuaScriptSuite<>(L).test();
        }
    }

    @Test
    public void luaJTest() {
        org.junit.Assume.assumeTrue(android.os.Build.VERSION.SDK_INT >= 30);
        try (AbstractLua L = AndroidLuaTest.getLuaJ()) {
            new LuaScriptSuite<>(L).test();
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
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            Lua L = AndroidLuaTest.getLuaJ();
            LuaScriptSuite.memoryTest(L);
            L.close();
        }
    }
}
