package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;

public class ProxyExampleTest {
    @Test
    public void runnableTest() {
// #region runnableTest
try (Lua L = new Lua54()) {
    L.run("r = { run = function() print('Hello') end }; return r");
    // With LuaValue API
    Runnable r = L.get("r").toProxy(Runnable.class);
    r.run();
    // With stack-based API
    Runnable s = (Runnable) L.createProxy(
        new Class[]{Runnable.class},
        Lua.Conversion.SEMI
    );
    s.run();
}
// #endregion runnableTest
    }
}
