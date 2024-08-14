package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;

public class RtldExampleTest {
    @Disabled
    @Test
    public void loadAsGlobalTest() {
// #region loadAsGlobalTest
try (Lua L = new Lua54()) {
    L.getLuaNatives().loadAsGlobal();
    L.run("require('lfs')");
}
// #endregion loadAsGlobalTest
    }
}
