package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
import party.iroiro.luajava.*;
import party.iroiro.luajava.value.LuaValueSuite;

public class LuaValueTest {
    public static final int REPEATED = 20;

    @RepeatedTest(REPEATED)
    public void lua51Test() {
        new LuaValueSuite<>(new Lua51()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua52Test() {
        new LuaValueSuite<>(new Lua52()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua53Test() {
        new LuaValueSuite<>(new Lua53()).test();
    }

    @RepeatedTest(REPEATED)
    public void lua54Test() {
        new LuaValueSuite<>(new Lua54()).test();
    }

    @RepeatedTest(REPEATED)
    public void luaJitTest() {
        new LuaValueSuite<>(new LuaJit()).test();
    }
}
