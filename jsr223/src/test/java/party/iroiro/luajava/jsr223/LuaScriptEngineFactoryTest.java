package party.iroiro.luajava.jsr223;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LuaScriptEngineFactoryTest {
    @Test
    public void simpleTest() {
        LuaScriptEngineFactory factory = new LuaScriptEngineFactory();
        assertInstanceOf(LuaScriptEngine.class, factory.getScriptEngine());
    }

    @Test
    public void preferredVersionTest() {
        System.setProperty("luajava.jsr-223", "54");
        new LuaScriptEngineFactory();
        System.setProperty("luajava.jsr-223", "jit");
        new LuaScriptEngineFactory();
        System.setProperty("luajava.jsr-223", "nope");
        new LuaScriptEngineFactory();
        System.clearProperty("luajava.jsr-223");
        new LuaScriptEngineFactory();
    }

    @Test
    public void notFoundTest() throws NoSuchFieldException, IllegalAccessException {
        Field engineField = LuaScriptEngineFactory.class.getDeclaredField("ENGINES");
        engineField.setAccessible(true);
        String[][] engines = (String[][]) engineField.get(null);
        ArrayList<String> stash = new ArrayList<>(engines.length);
        for (String[] engine : engines) {
            stash.add(engine[2]);
            engine[2] = "nonsense";
        }
        assertThrows(LinkageError.class, LuaScriptEngineFactory::new);
        for (int i = 0; i < engines.length; i++) {
            engines[i][2] = stash.get(i);
        }
    }

    @Test
    public void paramTest() {
        LuaScriptEngineFactory factory = new LuaScriptEngineFactory();
        assertEquals("Lua 5.4", factory.getEngineName());
        assertEquals("5.4.4", factory.getEngineVersion());
        assertIterableEquals(Collections.singletonList("lua"), factory.getExtensions());
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add("text/x-lua");
        mimeTypes.add("application/x-lua");
        assertIterableEquals(mimeTypes, factory.getMimeTypes());
        assertIterableEquals(Collections.singletonList("lua54"), factory.getNames());
        assertEquals("Lua", factory.getLanguageName());
        assertEquals("5.4.4", factory.getLanguageVersion());

        String[][] params = {
                {"ENGINE", "Lua 5.4"},
                {"ENGINE_VERSION", "5.4.4"},
                {"LANGUAGE", "Lua"},
                {"LANGUAGE_VERSION", "5.4.4"},
                {"NAME", "lua54"},
                {"THREADING", "THREAD-ISOLATED"},
                {"NONSENSE", null},
        };
        for (String[] param : params) {
            assertEquals(param[1], factory.getParameter(param[0]));
        }
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> factory.getParameter(null));
    }

    @Test
    public void concatTest() {
        LuaScriptEngineFactory factory = new LuaScriptEngineFactory();
        assertEquals(
                "o:m(a, b, c)",
                factory.getMethodCallSyntax("o", "m", "a", "b", "c")
        );
        assertEquals(
                "print(1);\nprint(2);\n",
                factory.getProgram("print(1)", "print(2)")
        );
        assertEquals(
                "print(\"\\x61\")",
                factory.getOutputStatement("a")
        );
    }
}