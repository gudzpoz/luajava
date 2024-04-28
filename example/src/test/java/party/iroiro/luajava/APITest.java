package party.iroiro.luajava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.luaj.LuaJ;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

@Testable
public class APITest {
    public static void assertTrue(boolean t) {
        Assertions.assertTrue(t);
    }

    @Test
    public void apiTest51() {
        apiTest(new Lua51());
    }

    @Test
    public void apiTestJ() {
        apiTest(new LuaJ());
    }

    public void apiTest(Lua L) {
        L.openLibrary("string");
        L.push(sum);
        L.setGlobal("sum");
        L.setExternalLoader(new ClassPathLoader());
        L.loadExternal("tests.apiTest");
        L.pCall(0, Consts.LUA_MULTRET);
        L.run("System.out:println(instance.testPrivate)");
        L.run("System.out:println(instance.testFriendly)");
        assertThrows(LuaException.class, () -> L.run("APITest:assert(false)"));

        byte[] bytes = "System.out:println('OK')".getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        L.run(byteBuffer, "ok");
        assertThrowsLua(LuaException.LuaError.MEMORY, () -> L.run(ByteBuffer.allocate(0), "notOk"));
        assertThrowsLua(LuaException.LuaError.MEMORY, () -> L.load(ByteBuffer.allocate(0), "notOk"));

        assertEquals(LuaException.LuaError.OK, L.status());

        assertThrows(UnsupportedOperationException.class,
                () -> L.yield(1));
    }

    private final int testPrivate = 443;
    final int testFriendly = 443;
    public final int testPublic = 443;
    public static double[] array;
    public static double[][] arrays;
    public static final double sum;
    static {
        double calSum = 0;
        int len = (int) (Math.random() * 20 + 10);
        array = new double[len];
        arrays = new double[len][len];
        for (int i = 0; i < len; i++) {
            array[i] = Math.random();
            calSum += array[i];
        }
        sum = calSum;
        for (int i = 0; i < len; i++) {
            arrays[i] = array;
        }
    }
}
