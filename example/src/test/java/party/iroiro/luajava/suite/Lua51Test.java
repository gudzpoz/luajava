package party.iroiro.luajava.suite;

import org.junit.jupiter.api.RepeatedTest;
import party.iroiro.luajava.Lua51;
import party.iroiro.luajava.LuaTestSuite;

public class Lua51Test {
    @RepeatedTest(20)
    public void lua51Test() {
        try (Lua51 L = new Lua51()) {
            new LuaTestSuite<>(L).test();
        }
    }
}
