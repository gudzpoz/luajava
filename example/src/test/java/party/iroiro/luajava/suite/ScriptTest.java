package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
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
}
