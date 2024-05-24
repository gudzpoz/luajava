package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.ArrayList;
import java.util.function.Supplier;

public class LuaCodeExampleTest {
    private final static String[] TEST_FILES = new String[] {
            "apiArrayExample",
            "apiClazzExample",
            "apiImportExample",
            "apiLoadlibExample",
            "apiMethodExample1",
            "apiMethodExample2",
            "apiMethodExample3",
            "apiMethodExample",
            "apiNewExample",
            "apiObjectExample",
            "apiProxyExampleDisabled",
            "apiVarargsExample",
            "conversions64BitExample",
            "modulesRequireExample",
            "proxyExampleTest",
    };

    @Test
    public void testLuaCode() {
        ArrayList<Supplier<Lua>> lua = new ArrayList<>();
        lua.add(Lua51::new);
        lua.add(Lua52::new);
        lua.add(Lua52::new);
        lua.add(Lua54::new);
        lua.add(LuaJit::new);
        lua.add(LuaJ::new);
        for (String file : TEST_FILES) {
            if (file.endsWith("Disabled")) {
                continue;
            }
            for (Supplier<Lua> supplier : lua) {
                try (Lua L = supplier.get()) {
                    if (file.equals("conversions64BitExample")) {
                        if (!(L instanceof Lua54)) {
                            continue;
                        }
                    }
                    L.openLibraries();
                    L.setExternalLoader(new ClassPathLoader());
                    L.run("print = function() end");
                    L.run("oldImport = java.import; " +
                            "java.import = function(s) " +
                            "  if string.sub('abcdefg', 0, 7) == 'android' then " +
                            "    return { id = { input = '' } };" +
                            "  else " +
                            "    return oldImport(s);" +
                            "  end " +
                            "end");
                    L.loadExternal("docs." + file);
                    L.pCall(0, 0);
                }
            }
        }
    }
}
