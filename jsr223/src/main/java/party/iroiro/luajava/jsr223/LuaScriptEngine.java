package party.iroiro.luajava.jsr223;

import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.util.ClassUtils;
import party.iroiro.luajava.value.LuaValue;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;

public final class LuaScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable {
    private final String luaClass;
    private final LuaScriptEngineFactory factory;

    LuaScriptEngine(String luaClass, LuaScriptEngineFactory factory) {
        this.luaClass = luaClass;
        this.factory = factory;
    }

    private Lua getLua() throws ScriptException {
        try {
            Lua L = (Lua) ClassUtils.forName(luaClass).newInstance();
            L.setExternalLoader(new ClassPathLoader());
            L.openLibraries();
            return L;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ScriptException(e);
        }
    }

    private void putContext(Lua L, ScriptContext scriptContext) {
        Bindings global = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
        if (global != null) {
            global.forEach((k, v) -> {
                L.push(v, Lua.Conversion.SEMI);
                L.setGlobal(k);
            });
        }
        scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).forEach((k, v) -> {
            L.push(v, Lua.Conversion.SEMI);
            L.setGlobal(k);
        });
    }

    @Override
    public Object eval(String script, ScriptContext scriptContext) throws ScriptException {
        assertNotNull(script, "script");
        assertNotNull(scriptContext, "context");
        try (Lua L = getLua()) {
            putContext(L, scriptContext);
            LuaValue[] values = L.eval(script);
            return values.length == 0 ? null : values;
        } catch (LuaException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
        return eval(readAll(reader), scriptContext);
    }

    private String readAll(Reader reader) throws ScriptException {
        assertNotNull(reader, "reader");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String code = bufferedReader.lines().collect(Collectors.joining("\n"));
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return code;
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    private static <T> void assertNotNull(T t, String name) {
        if (t == null) {
            throw new NullPointerException(name + " must not be null");
        }
    }

    @Override
    public CompiledScript compile(String s) throws ScriptException {
        try (Lua L = getLua()) {
            L.load(s);
            ByteBuffer dump = L.dump();
            return new CompiledScript() {
                @Override
                public Object eval(ScriptContext scriptContext) throws ScriptException {
                    assertNotNull(scriptContext, "context");
                    try (Lua L = getLua()) {
                        putContext(L, scriptContext);
                        int top = L.getTop();
                        L.load(dump, "CompiledScript");
                        L.pCall(0, Consts.LUA_MULTRET);
                        int returnCount = L.getTop() - top;
                        LuaValue[] returnValues = new LuaValue[returnCount];
                        for (int i = 0; i < returnCount; i++) {
                            returnValues[returnCount - i - 1] = L.get();
                        }
                        return returnValues.length == 0 ? null : returnValues;
                    } catch (LuaException e) {
                        throw new ScriptException(e);
                    }
                }

                @Override
                public ScriptEngine getEngine() {
                    return LuaScriptEngine.this;
                }
            };
        } catch (LuaException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public CompiledScript compile(Reader reader) throws ScriptException {
        return compile(readAll(reader));
    }
}
