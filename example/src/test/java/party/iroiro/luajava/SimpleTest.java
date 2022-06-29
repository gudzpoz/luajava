package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.Lua.LuaError.OK;

@Testable
public class SimpleTest {
    @Test
    public void simpleTest() {
        Lua L = new Lua51();
        L.push("Hello World from Lua");
        L.setGlobal("message");
        assertEquals(OK, L.run("print(message)"));
        assertEquals(OK, L.run("java.import('java.lang.System').out:println(message)"));
        L.close();
    }
}
