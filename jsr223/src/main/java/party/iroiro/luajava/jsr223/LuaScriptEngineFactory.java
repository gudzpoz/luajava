package party.iroiro.luajava.jsr223;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.util.ClassUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuaScriptEngineFactory implements ScriptEngineFactory {
    private final static List<String> MIME_TYPES;
    private final static List<String> EXTENSIONS;

    static {
        ArrayList<String> mimeTypes = new ArrayList<>(2);
        mimeTypes.add("text/x-lua");
        mimeTypes.add("application/x-lua");
        MIME_TYPES = Collections.unmodifiableList(mimeTypes);
        EXTENSIONS = Collections.singletonList("lua");
    }

    private final String name;
    private final String version;
    private final List<String> names;
    private final String luaClass;

    /**
     * Creates a factory instance
     *
     * <p>
     * This method is used by {@link java.util.ServiceLoader ServiceLoaders} to
     * instantiate engine factories.
     * </p>
     */
    public LuaScriptEngineFactory() {
        this(findAvailableEngine());
    }

    protected LuaScriptEngineFactory(String[] engine) {
        this(engine[0], engine[1], engine[2]);
    }

    /**
     * Creates a factory instance
     *
     * @param name     the engine name
     * @param version  the engine version
     * @param luaClass the fully qualified name of the {@link Lua} implementation class
     */
    public LuaScriptEngineFactory(String name, String version, String luaClass) {
        this.name = name;
        this.version = version;
        this.luaClass = luaClass;

        names = Collections.singletonList(name.toLowerCase()
                .replace(" ", "")
                .replace(".", ""));
    }

    @Override
    public String getEngineName() {
        return name;
    }

    @Override
    public String getEngineVersion() {
        return version;
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getLanguageName() {
        return "Lua";
    }

    @Override
    public String getLanguageVersion() {
        return version;
    }

    @Override
    public Object getParameter(String s) {
        switch (s) {
            case "ENGINE":
                return getEngineName();
            case "ENGINE_VERSION":
                return getEngineVersion();
            case "LANGUAGE":
                return getLanguageName();
            case "LANGUAGE_VERSION":
                return getLanguageVersion();
            case "NAME":
                return getNames().get(0);
            case "THREADING":
                return "THREAD-ISOLATED";
            default:
                return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return obj + ":" + m + "(" + String.join(", ", args) + ")";
    }

    @Override
    public String getOutputStatement(String s) {
        return escape(s, "print(\"", "\")");
    }

    @Override
    public String getProgram(String... strings) {
        int length = strings.length * 2;
        for (String string : strings) {
            length += string.length();
        }
        StringBuilder builder = new StringBuilder(length);
        for (String string : strings) {
            builder.append(string).append(";\n");
        }
        return builder.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new LuaScriptEngine(luaClass, this);
    }

    private final static char[] HEX_LOOKUP = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    };

    @SuppressWarnings("SameParameterValue")
    private static String escape(String s, String prefix, String suffix) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder(bytes.length * 4 + prefix.length() + suffix.length());
        builder.append(prefix);
        for (byte b : bytes) {
            builder.append("\\x");
            builder.append(HEX_LOOKUP[b >> 4]);
            builder.append(HEX_LOOKUP[b & 0x0F]);
        }
        builder.append(suffix);
        return builder.toString();
    }

    private final static String[][] ENGINES = {
            {"Lua 5.4", "5.4.4", "party.iroiro.luajava.lua54.Lua54", "54"},
            {"Lua 5.3", "5.3.6", "party.iroiro.luajava.lua53.Lua53", "53"},
            {"Lua 5.2", "5.2.4", "party.iroiro.luajava.lua52.Lua52", "52"},
            {"LuaJ", "5.2.4", "party.iroiro.luajava.luaj.LuaJ", "j"},
            {"LuaJIT", "6c4826f12c4d33b8b978004bc681eb1eef2be977",
                    "party.iroiro.luajava.luajit.LuaJit", "jit"},
            {"Lua 5.1", "5.1.5", "party.iroiro.luajava.lua51.Lua51", "51"},
    };

    private static String[] findAvailableEngine() {
        String wanted = System.getProperty("luajava.jsr-223");
        if (wanted != null) {
            for (String[] engine : ENGINES) {
                if (engine[3].equals(wanted) || ("lua" + engine[3]).equals(wanted)) {
                    return engine;
                }
            }
        }
        for (String[] engine : ENGINES) {
            try {
                ClassUtils.forName(engine[2]);
                return engine;
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new LinkageError("No available Lua provider found");
    }
}
