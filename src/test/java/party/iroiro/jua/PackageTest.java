package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PackageTest {
    @Test
    public void packageTest() {
        packageRequireTest("table");
        packageRequireTest("bit");
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
            assertNotEquals(Lua.LuaError.OK, L.run(s));
            assertNotEquals(-1, L.toString(-1).indexOf("attempt to call global 'require'"));
        }
        if (!name.equals("package")) {
            try (Lua L = new LuaJit()) {
                L.openLibrary("package");
                assertNotEquals(Lua.LuaError.OK, L.run(s));
                assertNotEquals(-1, L.toString(-1).indexOf("module '" + name + "' not found"));
            }
        }
        try (Lua L = new LuaJit()) {
            L.openLibrary("package");
            L.openLibrary(name);
            assertEquals(Lua.LuaError.OK, L.run(s));
        }
    }
}
