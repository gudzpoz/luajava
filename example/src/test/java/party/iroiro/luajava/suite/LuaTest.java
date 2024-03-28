package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
import party.iroiro.luajava.*;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

public class LuaTest {
    public static final int REPEATED = 20;

    @RepeatedTest(REPEATED)
    public void lua51Test() {
        try (Lua51 L = new Lua51()) {
            new LuaTestSuite<>(L, Lua51::new).test();
        }
    }
    @RepeatedTest(REPEATED)
    public void lua52Test() {
        try (Lua52 L = new Lua52()) {
            new LuaTestSuite<>(L, Lua52::new).test();
        }
    }
    @RepeatedTest(REPEATED)
    public void lua53Test() {
        try (Lua53 L = new Lua53()) {
            new LuaTestSuite<>(L, Lua53::new).test();
        }
    }
    @RepeatedTest(REPEATED)
    public void lua54Test() {
        try (Lua54 L = new Lua54()) {
            new LuaTestSuite<>(L, Lua54::new).test();
        }
    }
    @RepeatedTest(REPEATED)
    public void luaJitTest() {
        try (LuaJit L = new LuaJit()) {
            new LuaTestSuite<>(L, LuaJit::new).test();
        }
    }

    @RepeatedTest(REPEATED)
    public void luaJTest() {
        try (LuaJ L = new LuaJ()) {
            new LuaTestSuite<>(L, LuaJ::new).test();
        }
    }
}
