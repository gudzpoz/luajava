package party.iroiro.luajava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.Lua.LuaError.OK;

/**
 * Testing {@link JuaAPI} from lua side
 *
 * <p>
 * One might want to manually run this test, and then generate jacoco reports to verify real full coverage.
 * </p>
 */
public class JuaApiLuaTest {
    public static void assertTrue(boolean bool) {
        Assertions.assertTrue(bool);
    }

    @Test
    public void juaApiLuaTest() {
        try (Lua L = new Lua51()) {
            L.register("jfun", L1 -> 0);
            L.openLibraries();
            L.register("juafun", l -> 0);
            L.push(array, Lua.Conversion.NONE);
            L.setGlobal("arr");
            ResourceLoader loader = new ResourceLoader();
            loader.load("/tests/juaApiTest.lua", L);
            assertEquals(OK, L.pCall(0, Consts.LUA_MULTRET), () -> L.toString(-1));

            assertEquals(100, staticField);
            assertEquals(1024, privateField);
            assertEquals(100, t.s);
            assertEquals(1024, t.p);

            adoptTest(L);

            assertError(L, "t:nonexistentMethod()", "No matching method found");
            assertError(L, "t:privateMethod()", "No matching method found");
            assertError(L, "t:staticMethod({a = 1})", "No matching method found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void adoptTest(Lua L) {
        assertEquals(OK, L.run("" +
                "coroutine.resume(coroutine.create(function()" +
                "java.import('party.iroiro.luajava.JuaApiLuaTest').staticField = 200 end" +
                "))"));
        assertEquals(200, staticField);
    }

    private void assertError(Lua L, String lua, String message) {
        assertEquals(OK, L.load(lua));
        assertEquals(Lua.LuaError.RUNTIME, L.pCall(0, Consts.LUA_MULTRET));
        assertTrue(Objects.requireNonNull(L.toString(-1)).contains(message));
        L.setTop(0);
    }

    public static int staticField = 1024;
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static int privateField = 1024;
    public final static JuaApiLuaTest t = new JuaApiLuaTest();
    public int s = 1024;
    @SuppressWarnings("FieldMayBeFinal")
    private int p = 1024;

    public static int staticMethod(int a, int b, int c) {
        return a + b + c;
    }
    public static void staticMethod() {}
    public static int staticMethod(int a) { return a; }

    public static void getVoid() {}
    public static Object getNull() { return null; }

    private static int privateMethod() { return -1; }

    public void method() {}

    public static int[] array = new int[] {1, 2, 3, 4, 5};
}
