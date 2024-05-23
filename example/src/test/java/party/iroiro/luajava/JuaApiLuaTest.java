package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

/**
 * Testing {@link JuaAPI} from lua side
 *
 * <p>
 * One might want to manually run this test, and then generate jacoco reports to verify real full coverage.
 * </p>
 */
public class JuaApiLuaTest {
    @Test
    public void juaApiLuaTest() {
        try (Lua L = new Lua51()) {
            L.register("jfun", (l, args) -> null);
            L.openLibraries();
            L.register("juafun", (l, args) -> null);
            L.push(array, Lua.Conversion.NONE);
            L.setGlobal("arr");
            LuaScriptSuite.addAssertThrows(L);
            L.setExternalLoader(new ClassPathLoader());
            L.loadExternal("tests.juaApiTest");
            L.pCall(0, Consts.LUA_MULTRET);

            assertEquals(100, staticField);
            assertEquals(1024, privateField);
            assertEquals(100, t.s);
            assertEquals(1024, t.p);

            adoptTest(L);

            assertError(L, "t:nonexistentMethod()", "no matching method found");
            assertError(L, "t:privateMethod()", "no matching method found");
            assertError(L, "t:staticMethod({a = 1})", "no matching method found");
        }
    }

    private void adoptTest(Lua L) {
        L.run("coroutine.resume(coroutine.create(function()" +
                "java.import('party.iroiro.luajava.JuaApiLuaTest').staticField = 200 end" +
                "))");
        assertEquals(200, staticField);
    }

    private void assertError(Lua L, String lua, @SuppressWarnings("SameParameterValue") String message) {
        assertThrowsLua(L, lua, LuaException.LuaError.RUNTIME, message);
        L.setTop(0);
    }

    public static int staticField = 1024;
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static int privateField = 1024;
    public final static JuaApiLuaTest t = new JuaApiLuaTest();
    public int s = 1024;
    @SuppressWarnings("FieldMayBeFinal")
    private int p = 1024;

    @SuppressWarnings("unused")
    public static int staticMethod(int a, int b, int c) {
        return a + b + c;
    }

    @SuppressWarnings("unused")
    public static void staticMethod() {
    }

    @SuppressWarnings("unused")
    public static int staticMethod(int a) {
        return a;
    }

    @SuppressWarnings("unused")
    public static void getVoid() {
    }

    @SuppressWarnings("unused")
    public static Object getNull() {
        return null;
    }

    @SuppressWarnings("unused")
    private static int privateMethod() {
        return -1;
    }

    @SuppressWarnings("unused")
    public void method() {
    }

    public static int[] array = new int[]{1, 2, 3, 4, 5};
}
