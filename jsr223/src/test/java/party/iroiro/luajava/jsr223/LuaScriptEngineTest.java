package party.iroiro.luajava.jsr223;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.value.LuaValue;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class LuaScriptEngineTest {
    private static LuaScriptEngine lua;

    @BeforeAll
    static void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        lua = (LuaScriptEngine) manager.getEngineByExtension("lua");
    }

    @Test
    public void scriptEngineFactoryTest() {
        ScriptEngineManager manager = new ScriptEngineManager();
        assertInstanceOf(LuaScriptEngine.class, manager.getEngineByExtension("lua"));
        assertInstanceOf(LuaScriptEngine.class, manager.getEngineByName("lua54"));
        assertInstanceOf(LuaScriptEngine.class, manager.getEngineByMimeType("text/x-lua"));
        assertInstanceOf(LuaScriptEngine.class, manager.getEngineByExtension("lua"));
    }

    @Test
    public void evalTest() throws ScriptException {
        LuaValue[] values = (LuaValue[]) lua.eval("assert('Hello')");
        assertNull(values);
        values = (LuaValue[]) lua.eval("return 1");
        assertEquals(1, values.length);
        assertEquals(1., values[0].toJavaObject());

        values = (LuaValue[]) lua.eval(new StringReader("assert('Hello')"));
        assertNull(values);
    }

    @Test
    public void compiledTest() throws ScriptException {
        LuaValue[] values = (LuaValue[]) ((Compilable) lua).compile("assert('Hello')").eval();
        assertNull(values);
        values = (LuaValue[]) ((Compilable) lua).compile("return 1").eval();
        assertEquals(1., values[0].toJavaObject());
        values = (LuaValue[]) ((Compilable) lua).compile(new StringReader("return 1")).eval();
        assertEquals(1., values[0].toJavaObject());
    }

    @Test
    public void scopeTest() throws ScriptException {
        lua.setBindings(null, ScriptContext.GLOBAL_SCOPE);
        lua.eval("assert(true)");
        lua.setBindings(new SimpleBindings(), ScriptContext.GLOBAL_SCOPE);
        lua.getBindings(ScriptContext.GLOBAL_SCOPE).put("javaTest1", "scopeTest");
        lua.getBindings(ScriptContext.ENGINE_SCOPE).put("javaTest2", "scopeTest");
        assertDoesNotThrow(() -> lua.eval("assert(javaTest1 .. javaTest2)"));
    }

    @Test
    public void exceptionTest() {
        assertThrows(ScriptException.class, () ->
                lua.eval("print(nil .. 1)"));
        assertThrows(NullPointerException.class, () -> lua.eval((Reader) null));
        //noinspection NullableProblems
        assertThrows(ScriptException.class, () -> lua.eval(new Reader() {
            @Override
            public int read(char[] chars, int i, int i1) {
                return -1;
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        }));

        Compilable compilable = lua;
        assertThrows(ScriptException.class, () ->
                compilable.compile("((("));
        assertThrows(ScriptException.class, () ->
                compilable.compile("print(nil .. 1)").eval());

        assertThrows(ScriptException.class,
                () -> new LuaScriptEngine("nonsense", null).eval("print()"));
    }

    @Test
    public void stubTest() {
        assertInstanceOf(LuaScriptEngineFactory.class, lua.getFactory());
        assertInstanceOf(SimpleScriptContext.class, lua.getContext());
        assertInstanceOf(SimpleBindings.class, lua.createBindings());
    }
}
