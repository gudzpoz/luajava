package party.iroiro.luajava.jpms;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

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
                new LuaJ(),
        };
    }

    private void test() {
        for (Lua L : luas) {
            // Accessing java.*
            L.run("String = java.import('java.lang.String')");
            L.run("s = 'Hello World: ' .. _VERSION; assert(String(s):toString() == s)");
            // Accessing explicitly modulized classes
            L.run("c = 'party.iroiro.luajava.jpms.Main'; assert(java.import(c).class:getName() == c)");
            // Accessing implicit modules
            L.run("c = 'party.iroiro.luajava.lua51.Lua51'; assert(java.import(c).class:getName() == c)");
        }
    }

    @Override
    public void close() {
        for (Lua L : luas) {
            L.close();
        }
    }
}
