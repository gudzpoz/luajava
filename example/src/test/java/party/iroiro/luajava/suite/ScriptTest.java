package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.*;

public class ScriptTest {
    public static final int REPEATED = 30;

    @RepeatedTest(REPEATED)
    public void lua51Test() {
        new LuaScriptSuite<>(new Lua51()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua52Test() {
        new LuaScriptSuite<>(new Lua52()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua53Test() {
        new LuaScriptSuite<>(new Lua53()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua54Test() {
        new LuaScriptSuite<>(new Lua54()).test();
    }

    @RepeatedTest(REPEATED)
    public void luaJitTest() {
        new LuaScriptSuite<>(new LuaJit()).test();
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
