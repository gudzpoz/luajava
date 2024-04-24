package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.*;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

public class ScriptTest {
    public static final int REPEATED = 30;

    @RepeatedTest(REPEATED)
    public void lua51Test() {
        try (Lua51 L = new Lua51()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void lua52Test() {
        try (Lua52 L = new Lua52()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void lua53Test() {
        try (Lua53 L = new Lua53()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void lua54Test() {
        try (Lua54 L = new Lua54()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void luaJitTest() {
        try (LuaJit L = new LuaJit()) {
            new LuaScriptSuite<>(L).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void luaJTest() {
        try (LuaJ L = new LuaJ()) {
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
                new LuaJ(),
        };
        for (Lua L : Ls) {
            LuaScriptSuite.memoryTest(L);
            L.close();
        }
    }
}
