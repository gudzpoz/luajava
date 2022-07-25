package party.iroiro.luajava.suite;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.LuaProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultProxyTest {
    public interface DefaultRunnable extends Callable<Integer> {
        @Override
        default Integer call() {
            return 1024;
        }

        default void throwsError() {
            throw new LuaException("exception!");
        }

        boolean equals();

        void luaError();

        int luaError(int i);
    }

    private final Lua L;

    public DefaultProxyTest(Lua L) {
        this.L = L;
    }

    public void testMethodEquals() throws Throwable {
        Method equals = Object.class.getMethod("equals", Object.class);
        Method hashCode = Object.class.getMethod("hashCode");
        Method toString = Object.class.getMethod("toString");
        assertTrue(
                LuaProxy.methodEquals(equals, boolean.class, "equals", Object.class)
        );
        assertTrue(
                LuaProxy.methodEquals(hashCode, int.class, "hashCode")
        );
        assertTrue(
                LuaProxy.methodEquals(toString, String.class, "toString")
        );
        assertFalse(
                LuaProxy.methodEquals(equals, int.class, "equals", Object.class)
        );
        assertFalse(
                LuaProxy.methodEquals(equals, boolean.class, "equal", Object.class)
        );
        assertFalse(
                LuaProxy.methodEquals(equals, boolean.class, "equals", Integer.class)
        );
    }

    public void test() {
        assertDoesNotThrow(this::testMethodEquals);
        L.run("return { luaError = function(_, i)\n" +
              "assert(i == 2 or i == 3)\n" +
              "if i == 2 then return nil else return 3 end\n" +
              "end }");
        DefaultRunnable proxy =
                (DefaultRunnable) L.createProxy(new Class[]{DefaultRunnable.class}, Lua.Conversion.SEMI);
        assertEquals(1024, proxy.call());
        assertEquals(Proxy.getInvocationHandler(proxy).hashCode(), proxy.hashCode());
        // noinspection SimplifiableAssertion,EqualsWithItself
        assertTrue(proxy.equals(proxy));
        // noinspection SimplifiableAssertion,EqualsBetweenInconvertibleTypes
        assertFalse(proxy.equals(L));
        assertEquals("LuaProxy:interface party.iroiro.luajava.suite.DefaultProxyTest$DefaultRunnable,[]@"
                     + Integer.toHexString(proxy.hashCode()), proxy.toString());
        LuaException exception = assertThrows(LuaException.class, proxy::equals);
        assertTrue(exception.getMessage().startsWith("method not implemented: "));

        assertEquals("exception!",
                assertThrows(LuaException.class, proxy::throwsError).getMessage());

        assertTrue(assertThrows(LuaException.class, proxy::luaError).getMessage()
                .contains("assertion failed"));
        assertTrue(assertThrows(LuaException.class, () -> proxy.luaError(1)).getMessage()
                .contains("assertion failed"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> proxy.luaError(2)).getMessage()
                .contains("Primitive not accepting null values"));
        assertEquals(3., proxy.luaError(3));

        hierarchyTest();
    }

    private void hierarchyTest() {
        L.createTable(0, 0);
        Object proxy = L.createProxy(new Class[]{
                A.class, B.class, C.class,
        }, Lua.Conversion.SEMI);
        assertEquals(4, ((C) proxy).c());
        assertEquals(3, ((B) proxy).b());
    }

    interface A {
        default int a() {
            return 1;
        }
    }

    interface B {
        default int b() {
            return ((A) this).a() + 2;
        }
    }

    interface C extends A {
        default int c() {
            return a() + 3;
        }
    }
}
