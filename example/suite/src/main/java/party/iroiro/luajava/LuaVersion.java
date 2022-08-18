package party.iroiro.luajava;

import party.iroiro.luajava.interfaces.LuaTestBiConsumer;
import party.iroiro.luajava.interfaces.LuaTestSupplier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public enum LuaVersion {
    v51("5.1", Lua51::new),
    v52("5.2", Lua52::new),
    v53("5.3", Lua53::new),
    v54("5.4", Lua54::new),
    vjit("jit", LuaJit::new);

    public final String value;
    public final LuaTestSupplier<AbstractLua> supplier;

    LuaVersion(String value, LuaTestSupplier<AbstractLua> supplier) {
        this.value = value;
        this.supplier = supplier;
    }

    private final static Map<String, LuaVersion> VERSIONS;
    static {
        Map<String, LuaVersion> map = new HashMap<>();
        for (LuaVersion version : values()) {
            map.put(version.value, version);
        }
        VERSIONS = Collections.unmodifiableMap(map);
    }

    public static LuaVersion from(String value) {
        LuaVersion luaVersion = VERSIONS.get(value);
        if (luaVersion == null) {
            try {
                return valueOf("v" + value);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        } else {
            return luaVersion;
        }
    }

    public static void forEachTest(LuaTestBiConsumer<AbstractLua, LuaVersion> tester) {
        for (LuaVersion version : LuaVersion.values()) {
            try (AbstractLua L = version.supplier.get()) {
                tester.accept(L, version);
            } catch (Throwable e) {
                throw new AssertionError("Error testing " + version.value, e);
            }
        }
    }
}
