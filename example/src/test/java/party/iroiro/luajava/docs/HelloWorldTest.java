package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;

public class HelloWorldTest {
    @Test
    public void test() {
try (Lua L = new Lua51()) {
    L.openLibraries();
    L.run("System = java.import('java.lang.System')");
    L.run("System.out:println('Hello World from Lua!')");
}
    }
}
