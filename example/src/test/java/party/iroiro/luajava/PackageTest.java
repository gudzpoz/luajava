package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.luajit.LuaJit;

import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

public class PackageTest {
    @Test
    public void packageTest() {
        packageRequireTest("table");
        packageRequireTest("debug");
        packageRequireTest("io");
        packageRequireTest("math");
        packageRequireTest("os");
        packageRequireTest("string");
        packageRequireTest("package");
    }

    private void packageRequireTest(String name) {
        String s = "local r = require(\"" + name + "\")";
        try (Lua L = new LuaJit()) {
            assertThrowsLua(L, s, LuaException.LuaError.RUNTIME, "attempt to call global 'require'");
        }
        if (!name.equals("package")) {
            try (Lua L = new LuaJit()) {
                L.openLibrary("package");
                assertThrowsLua(L, s, LuaException.LuaError.RUNTIME, "module '" + name + "' not found");
            }
        }
        try (Lua L = new LuaJit()) {
            L.openLibrary("package");
            L.openLibrary(name);
            L.run(s);
        }
    }
}
