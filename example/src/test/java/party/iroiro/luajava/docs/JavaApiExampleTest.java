package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.value.LuaValue;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaApiExampleTest {
    @Test
    public void luaValueTest() {
try (Lua L = new Lua54()) {
    LuaValue[] returnValues = L.eval("return { a = 1 }, 1024, 'string'");
    assertEquals(3, returnValues.length);
    assertEquals(1, returnValues[0].get("a").toInteger());
    assertEquals(1024, returnValues[1].toInteger());
    assertEquals("string", returnValues[2].toString());
}
    }

    @SuppressWarnings({"EmptyTryBlock", "unused"})
    @Test
    public void closableTest() {
Lua L = new Lua51();
// Operations
L.close();

// Or
try (Lua J = new Lua51()) {
    // Operations
}
    }

    @Test
    public void globalSetTest() {
try (Lua L = new Lua54()) {
    // Use LuaValue-based API
    L.set("myStr", "string value");
    L.run("assert(myStr == 'string value')");
    // Or use stack-based API
    L.push("string value");
    L.setGlobal("myStr");
    L.run("assert(myStr == 'string value')");
}
    }

    @Test
    public void globalGetTest() {
try (Lua L = new Lua54()) {
    L.run("a = 1024");
    // Use LuaValue-based API
    assertEquals(1024, L.get("a").toInteger());
    // Or use stack-based API
    L.getGlobal("a");
    assertEquals(1024, L.toInteger(-1));
}
    }

    @Test
    public void getFieldTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.getField(-1, "a");       // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
    }

    @Test
    public void rawGetITest() {
try (Lua L = new Lua54()) {
    L.run("return { [20] = 1 }"); // Pushes a table on stack
    L.rawGetI(-1, 20);            // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
    }

    @Test
    public void getTableTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key to look up
    L.getTable(-2);            // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
    }

    @Test
    public void rawGetTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key to look up
    L.rawGet(-2);              // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
    }

    @Test
    public void setFieldTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push(2);                 // Pushes the new value
    L.setField(-2, "a");       // Updates the value
}
    }

    @Test
    public void rawSetITest() {
try (Lua L = new Lua54()) {
    L.run("return { [20] = 1 }"); // Pushes a table on stack
    L.push(2);                    // Pushes the new value
    L.rawSetI(-2, 20);            // Updates the value
}
    }

    @Test
    public void setTableTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key
    L.push(2);                 // Pushes the new value
    L.getTable(-3);            // Updates the value
}
    }

    @Test
    public void rawSetTest() {
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key
    L.push(2);                 // Pushes the new value
    L.rawSet(-3);              // Updates the value
}
    }

    @SuppressWarnings("SameParameterValue")
    private ByteBuffer readFromFile(String ignored) {
try (Lua L = new Lua54()) {
    L.load("return 0");
    return L.dump();
}
    }

    @Test
    public void luaDumpTest() {
try (Lua L = new Lua54()) {
    ByteBuffer code = readFromFile("MyScript.lua");
    // L.load(...) pushes on stack a precompiled function
    L.load(code, "MyScript.lua");
    // L.dump() calls lua_dump, dumping the precompiled binary
    ByteBuffer precompiledChunk = L.dump();
    L.load(precompiledChunk, "MyScript.precompiled");
}
    }

    @Test
    public void stringDumpTest() {
try (Lua L = new Lua54()) {
    L.openLibrary("string");
    // string.dump(...) returns the precompiled binary as a Lua string
    L.run("return string.dump(function(a, b) return a + b end)");
    // L.toBuffer(...) stores the precompiled binary into a buffer and returns it
    ByteBuffer precompiledChunk = L.toBuffer(-1);
    L.load(precompiledChunk, "MyScript.precompiled");
}
    }
}
