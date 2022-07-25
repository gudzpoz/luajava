package party.iroiro.luajava.suite;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.LuaProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaError.OK;

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
        assertEquals("LuaProxy[interface party.iroiro.luajava.suite.DefaultProxyTest$DefaultRunnable]@"
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
        simpleIterTest();
        exceptionTest();
    }

    private void exceptionTest() {
        L.run("return {}");
        assertEquals("java.lang.String is not an interface",
                assertThrows(IllegalArgumentException.class,
                        () -> L.createProxy(new Class[]{String.class}, Lua.Conversion.SEMI)).getMessage()
        );
        L.run("return {}");
        L.push(L.createProxy(new Class[]{A.class}, Lua.Conversion.SEMI), Lua.Conversion.NONE);
        L.setGlobal("aa");
        assertEquals(OK, L.run("return aa:a() + 1"), L.toString(-1));
        assertEquals(2., L.toNumber(-1));
    }

    private void simpleIterTest() {
        L.run("i = 10");
        assertEquals(OK, L.run("return {\n" +
                               "  next = function()\n" +
                               "    i = i - 1\n" +
                               "    return i\n" +
                               "  end,\n" +
                               "  hasNext = function()\n" +
                               "    return i > 0\n" +
                               "  end\n" +
                               "}"));
        Iterator<?> iter = (Iterator<?>)
                L.createProxy(new Class[]{Iterator.class}, Lua.Conversion.SEMI);
        Set<Double> iset = new HashSet<>();
        iter.forEachRemaining(i -> {
            if (i instanceof Double) {
                assertTrue(iset.add((Double) i));
            }
        });
        assertEquals(10, iset.size(), Arrays.toString(iset.toArray()));

        L.createTable(0, 0);
        assertEquals("Expecting a table and interfaces",
                assertThrows(IllegalArgumentException.class,
                        () -> L.createProxy(new Class[0], Lua.Conversion.SEMI))
                        .getMessage());
    }

    private void hierarchyTest() {
        L.createTable(0, 0);
        Object proxy = L.createProxy(new Class[]{
                A.class, B.class, C.class,
        }, Lua.Conversion.SEMI);
        assertEquals(4, ((C) proxy).c());
        assertEquals(3, ((B) proxy).b());

        L.createTable(0, 0);
        Object dup = L.createProxy(new Class[]{
                A.class, D.class,
        }, Lua.Conversion.SEMI);
        Set<Integer> s = new HashSet<>();
        s.add(1);
        s.add(2);
        assertTrue(s.contains(((A) dup).dup()));
        assertTrue(s.contains(((D) dup).dup()));
    }

    /* {@code public} is required */
    public interface A {
        default int a() {
            return 1;
        }

        default int dup() {
            return 1;
        }
    }

    interface B {
        default int b() {
            return ((A) this).a() + 2;
        }
    }

    interface D {
        default int dup() {
            return 2;
        }
    }

    interface C extends A {
        default int c() {
            return a() + 3;
        }
    }
}
