package party.iroiro.luajava.jpms;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luajit.LuaJit;

import static party.iroiro.luajava.Lua.LuaError.OK;

public class Main implements AutoCloseable {
    public static void main(String[] args) {
        try (Main test = new Main()) {
            test.test();
        }
    }

    private final Lua[] luas;

    private Main() {
        //noinspection resource
        luas = new Lua[] {
                new Lua51(),
                new Lua52(),
                new Lua53(),
                new Lua54(),
                new LuaJit(),
        };
    }

    private void test() {
        for (Lua L : luas) {
            // Accessing java.*
            assertEquals(L.run("out = java.import('java.lang.System').out"), OK);
            assertEquals(L.run("out:println('Hello World: ' .. _VERSION)"), OK);
            // Accessing explicitly modulized classes
            assertEquals(L.run("out:println(java.import('party.iroiro.luajava.jpms.Main').class:getName())"), OK);
            // Accessing implicit modules
            assertEquals(L.run("out:println(java.import('party.iroiro.luajava.lua51.Lua51').class:getName())"), OK);
        }
    }

    private void assertEquals(Object a, Object b) {
        if (a != b) {
            if (a != null && !a.equals(b)) {
                throw new AssertionError();
            }
        }
    }

    @Override
    public void close() {
        for (Lua L : luas) {
            L.close();
        }
    }
}
