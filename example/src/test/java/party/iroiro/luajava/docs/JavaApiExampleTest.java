package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.value.LuaValue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaApiExampleTest {
    @Test
    public void luaValueTest() {
// #region luaValueTest
try (Lua L = new Lua54()) {
    LuaValue[] returnValues = L.eval("return { a = 1 }, 1024, 'my string value'");
    assertEquals(3, returnValues.length);
    assertEquals(1, returnValues[0].get("a").toInteger());
    assertEquals(1024, returnValues[1].toInteger());
    assertEquals("my string value", returnValues[2].toString());
}
// #endregion luaValueTest
    }

    @Test
    public void luaValueFromGlobalTest() {
// #region luaValueFromGlobalTest
try (Lua L = new Lua54()) {
    assertEquals("Lua 5.4", L.get("_VERSION").toString());
}
// #endregion luaValueFromGlobalTest
    }

    @Test
    public void setGlobalTest() {
// #region setGlobalTest
try (Lua L = new Lua54()) {
    LuaValue value = L.from(1);
    L.set("a", value); // LuaValue
    L.set("b", 2); // Java Integer
    L.set("c", new BigDecimal(3)); // Any Java object
    assertEquals(
    6,
    L.eval("return a + b + c:longValue()")[0].toInteger()
    );
}
// #endregion setGlobalTest
    }

    @Test
    public void luaValueEvalTest() {
// #region luaValueEvalTest
try (Lua L = new Lua54()) {
    L.openLibraries();
    LuaValue[] values1 = L.eval("string.sub('abcdefg', 0, 3)");
    assertEquals(0, values1.length);
    LuaValue[] values2 = L.eval("return string.sub('abcdefg', 0, 3)");
    assertEquals("abc", values2[0].toString());
}
// #endregion luaValueEvalTest
    }

    @Test
    public void luaValueTableTest() {
// #region luaValueTableTest
try (Lua L = new Lua54()) {
    L.run("t = { text = 'abc', children = { 'a', 'b', 'c' } }");
    LuaValue table = L.eval("return t")[0];
    // Get-calls return LuaValues.
    assertEquals("abc", table.get("text").toString());
    LuaValue children = table.get("children");
    // Indices are 1-based.
    assertEquals("a", children.get(1).toString());
    assertEquals(3, children.size());
    // Set-calls accept LuaValues or any Java object.
    children.set(4, "d");
    // Changes are done in the Lua side.
    L.run("assert(t.children[4] == 'd')");
}
// #endregion luaValueTableTest
    }

    @Test
    public void luaValueCallTest() {
// #region luaValueCallTest
try (Lua L = new Lua54()) {
    L.openLibrary("string");
    LuaValue gsub = L.eval("return string.gsub")[0];
    LuaValue luaJava = gsub.call("Lua", "a", "aJava")[0];
    assertEquals("LuaJava", luaJava.toString());
}
// #endregion luaValueCallTest
    }

    @Test
    public void luaValueProxyTest() throws InterruptedException {
// #region luaValueProxyTest
try (Lua L = new Lua54()) {
    LuaValue runnable = L.eval("return { run = function() print('running...') end }")[0];
    Runnable r = runnable.toProxy(Runnable.class);
    Thread t = new Thread(r);
    t.start();
    t.join();
}
// #endregion luaValueProxyTest
    }

    @SuppressWarnings({"EmptyTryBlock", "unused"})
    @Test
    public void closableTest() {
// #region closableTest
Lua L = new Lua51();
// Operations
L.close();

// Or
try (Lua J = new Lua51()) {
    // Operations
}
// #endregion closableTest
    }

    @Test
    public void globalSetTest() {
// #region globalSetTest
try (Lua L = new Lua54()) {
    // Use LuaValue-based API
    L.set("myStr", "string value");
    L.run("assert(myStr == 'string value')");
    // Or use stack-based API
    L.push("string value");
    L.setGlobal("myStr");
    L.run("assert(myStr == 'string value')");
}
// #endregion globalSetTest
    }

    @Test
    public void globalGetTest() {
// #region globalGetTest
try (Lua L = new Lua54()) {
    L.run("a = 1024");
    // Use LuaValue-based API
    assertEquals(1024, L.get("a").toInteger());
    // Or use stack-based API
    L.getGlobal("a");
    assertEquals(1024, L.toInteger(-1));
}
// #endregion globalGetTest
    }

    @Test
    public void getFieldTest() {
// #region getFieldTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.getField(-1, "a");       // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
// #endregion getFieldTest
    }

    @Test
    public void rawGetITest() {
// #region rawGetITest
try (Lua L = new Lua54()) {
    L.run("return { [20] = 1 }"); // Pushes a table on stack
    L.rawGetI(-1, 20);            // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
// #endregion rawGetITest
    }

    @Test
    public void getTableTest() {
// #region getTableTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key to look up
    L.getTable(-2);            // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
// #endregion getTableTest
    }

    @Test
    public void rawGetTest() {
// #region rawGetTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key to look up
    L.rawGet(-2);              // Retrieves the value
    assertEquals(1, L.toInteger(-1));
}
// #endregion rawGetTest
    }

    @Test
    public void setFieldTest() {
// #region setFieldTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push(2);                 // Pushes the new value
    L.setField(-2, "a");       // Updates the value
}
// #endregion setFieldTest
    }

    @Test
    public void rawSetITest() {
// #region rawSetITest
try (Lua L = new Lua54()) {
    L.run("return { [20] = 1 }"); // Pushes a table on stack
    L.push(2);                    // Pushes the new value
    L.rawSetI(-2, 20);            // Updates the value
}
// #endregion rawSetITest
    }

    @Test
    public void setTableTest() {
// #region setTableTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key
    L.push(2);                 // Pushes the new value
    L.getTable(-3);            // Updates the value
}
// #endregion setTableTest
    }

    @Test
    public void rawSetTest() {
// #region rawSetTest
try (Lua L = new Lua54()) {
    L.run("return { a = 1 }"); // Pushes a table on stack
    L.push("a");               // Pushes the key
    L.push(2);                 // Pushes the new value
    L.rawSet(-3);              // Updates the value
}
// #endregion rawSetTest
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
// #region luaDumpTest
try (Lua L = new Lua54()) {
    ByteBuffer code = readFromFile("MyScript.lua");
    // L.load(...) pushes on stack a precompiled function
    L.load(code, "MyScript.lua");
    // L.dump() calls lua_dump, dumping the precompiled binary
    ByteBuffer precompiledChunk = L.dump();
    L.load(precompiledChunk, "MyScript.precompiled");
}
// #endregion luaDumpTest
    }

    @Test
    public void stringDumpTest() {
// #region stringDumpTest
try (Lua L = new Lua54()) {
    L.openLibrary("string");
    // string.dump(...) returns the precompiled binary as a Lua string
    L.run("return string.dump(function(a, b) return a + b end)");
    // L.toBuffer(...) stores the precompiled binary into a buffer and returns it
    ByteBuffer precompiledChunk = L.toBuffer(-1);
    L.load(precompiledChunk, "MyScript.precompiled");
}
// #endregion stringDumpTest
    }
}
