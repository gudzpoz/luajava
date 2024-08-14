package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;

public class ModuleSnippetTest {
    @Test
    public void classPathLoaderTest() {
// #region classPathLoaderTest
try (Lua L = new Lua51()) {
    L.openLibrary("package");
    L.setExternalLoader(new ClassPathLoader());
}
// #endregion classPathLoaderTest
    }

    @Test
    public void javaSideModuleTest() {
// #region javaSideModuleTest
try (Lua L = new Lua51()) {
    L.openLibrary("package");
    L.run("local LuaLib = require('party.iroiro.luajava.docs.JavaSideExampleModule.open');" +
    "assert(1024 == LuaLib.getNumber())");
}
// #endregion javaSideModuleTest
    }
}
