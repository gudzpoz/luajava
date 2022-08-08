package party.iroiro.luajava;

import party.iroiro.luajava.interfaces.LuaTestConsumer;
import party.iroiro.luajava.suite.B;
import party.iroiro.luajava.suite.InvokeSpecialConversionTest;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static party.iroiro.luajava.Lua.LuaError.OK;
import static party.iroiro.luajava.Lua.LuaError.RUNTIME;

public class DefaultProxyTest {
    private interface PrivateNullable {
        default void test(@SuppressWarnings("ConstantConditions") Object obj) {
            if (obj == null) {
                throw new NullPointerException("Passed a null value");
            }
        }
    }

    public interface DefaultRunnable extends Callable<Integer> {
        @Override
        default Integer call() {
            return 1024;
        }

        default void throwsError() {
            throw new LuaException("exception!");
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean equals();

        void luaError();

        int luaError(int i);
    }

    private final AbstractLua L;
    private final boolean defaultAvailable;
    private final boolean isAndroid;

    public DefaultProxyTest(AbstractLua L) {
        defaultAvailable = isDefaultAvailable();
        isAndroid = LuaScriptSuite.isAndroid();
        this.L = L;
    }

    public static boolean isDefaultAvailable() {
        try {
            Method.class.getMethod("isDefault");
            return true;
        } catch (Throwable e) {
            return false;
        }
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
        try {
            this.testMethodEquals();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        L.run("return { luaError = function(_, i)\n" +
              "assert(i == 2 or i == 3)\n" +
              "if i == 2 then return nil else return 3 end\n" +
              "end }");
        DefaultRunnable proxy =
                (DefaultRunnable) L.createProxy(new Class[]{DefaultRunnable.class}, Lua.Conversion.SEMI);
        /*
         * Our classes are desugared on Android and fail the tests.
         * Only java.* interfaces should be used to test default methods.
         */
        if (defaultAvailable && !isAndroid) {
            assertEquals(1024, (int) proxy.call());
        } else {
            assertTrue(assertThrows(LuaException.class, proxy::call)
                    .getMessage().startsWith("method not implemented: "));
        }
        assertEquals(Proxy.getInvocationHandler(proxy).hashCode(), proxy.hashCode());
        // noinspection SimplifiableAssertion,EqualsWithItself
        assertTrue(proxy.equals(proxy));
        // noinspection SimplifiableAssertion,EqualsBetweenInconvertibleTypes
        assertFalse(proxy.equals(L));
        assertEquals("LuaProxy[interface party.iroiro.luajava.DefaultProxyTest$DefaultRunnable]@"
                     + Integer.toHexString(proxy.hashCode()), proxy.toString());
        LuaException exception = assertThrows(LuaException.class, proxy::equals);
        assertTrue(exception.getMessage().startsWith("method not implemented: "));

        if (defaultAvailable && !isAndroid) {
            assertEquals("exception!",
                    assertThrows(LuaException.class, proxy::throwsError).getMessage());
        } else {
            assertTrue(assertThrows(LuaException.class, proxy::throwsError)
                    .getMessage().startsWith("method not implemented: "));
        }

        assertTrue(assertThrows(LuaException.class, proxy::luaError).getMessage()
                .contains("assertion failed"));
        assertTrue(assertThrows(LuaException.class, () -> proxy.luaError(1)).getMessage()
                .contains("assertion failed"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> proxy.luaError(2)).getMessage()
                .contains("Primitive not accepting null values"));
        assertEquals(3., proxy.luaError(3), 0.000001);

        hierarchyTest();
        simpleIterTest();
        exceptionTest();

        defaultMethodTest();
    }

    private void defaultMethodTest() {
        Iterator<Object> iterator = new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }

            @Override
            public void remove() {
            }
        };
        L.push(iterator, Lua.Conversion.SEMI);

        assertThrows((Class<? extends Throwable>) (defaultAvailable
                        ? UnsupportedOperationException.class
                        : IncompatibleClassChangeError.class),
                () -> L.invokeSpecial(iterator,
                        Iterator.class.getDeclaredMethod("remove"),
                        new Object[0]
                ));

        L.error((Throwable) null);
        L.run("i = 10");
        L.run("return {\n" +
              "  next = function()\n" +
              "    i = i - 1\n" +
              "    return i\n" +
              "  end,\n" +
              "  hasNext = function()\n" +
              "    return i > 0\n" +
              "  end" +
              "}");
        Iterator<?> iter = (Iterator<?>) L.createProxy(new Class[]{Iterator.class}, Lua.Conversion.SEMI);
        Set<Double> set = new HashSet<>();
        callForEachRemaining(iter, i -> {
            assertTrue(i instanceof Double);
            set.add(((Double) i));
        });
        if (defaultAvailable) {
            assertEquals(10, set.size());
            for (int i = 0; i < 10; i++) {
                assertTrue(set.contains((double) i));
            }
        } else {
            assertTrue(set.isEmpty());
        }

        L.run("return {}");
        PrivateNullable priv = (PrivateNullable) L.createProxy(new Class[]{PrivateNullable.class}, Lua.Conversion.SEMI);
        if (defaultAvailable && !isAndroid) {
            assertEquals(
                    "Passed a null value",
                    assertThrows(NullPointerException.class, () -> priv.test(null)).getMessage()
            );
        } else {
            assertTrue(
                    assertThrows(LuaException.class, () -> priv.test(null)).getMessage()
                            .startsWith("method not implemented: ")
            );
        }
        if (defaultAvailable && !isAndroid) {
            priv.test(new Object());
        } else {
            assertThrows(LuaException.class, () -> priv.test(new Object()));
        }

        if (defaultAvailable && !isAndroid) {
            new InvokeSpecialConversionTest(L).test();
        }
    }

    private void exceptionTest() {
        L.run("return {}");
        assertTrue(
                assertThrows(IllegalArgumentException.class,
                        () -> L.createProxy(new Class[]{String.class}, Lua.Conversion.SEMI))
                        .getMessage().contains("java.lang.String")
        );
        L.run("return {}");
        L.push(L.createProxy(new Class[]{A.class}, Lua.Conversion.SEMI), Lua.Conversion.NONE);
        L.setGlobal("aa");
        if (defaultAvailable && !isAndroid) {
            assertEquals(OK, L.run("return aa:a() + 1"));
            assertEquals(2., L.toNumber(-1), 0.000001);
        } else {
            assertEquals(RUNTIME, L.run("return aa:a() + 1"));
            assertTrue(L.toString(-1), Objects.requireNonNull(L.toString(-1))
                    .startsWith("party.iroiro.luajava.LuaException: method not implemented: "));
        }
    }

    private void callForEachRemaining(Iterator<?> iter, LuaTestConsumer<Object> testConsumer) {
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> consumer = Class.forName("java.util.function.Consumer");
            Method method = Iterator.class.getMethod("forEachRemaining", consumer);
            L.push(l -> {
                testConsumer.accept(l.toObject(-1));
                return 0;
            });
            Object impl = L.createProxy(new Class[]{consumer}, Lua.Conversion.SEMI);
            //noinspection JavaReflectionInvocation
            method.invoke(iter, impl);
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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
        callForEachRemaining(iter, i -> {
            assertTrue(i instanceof Double);
            assertTrue(iset.add((Double) i));
        });
        if (defaultAvailable) {
            assertEquals(10, iset.size());
        } else {
            assertEquals(0, iset.size());
        }

        L.createTable(0, 0);
        assertEquals("Expecting a table / function and interfaces",
                assertThrows(IllegalArgumentException.class,
                        () -> L.createProxy(new Class[0], Lua.Conversion.SEMI))
                        .getMessage());
    }

    private void hierarchyTest() {
        if (!defaultAvailable || isAndroid) {
            return;
        }

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

    public interface D {
        default int dup() {
            return 2;
        }

        @SuppressWarnings("unused")
        default void noReturn() {
        }
    }

    public interface C extends A {
        default int c() {
            return a() + 3;
        }
    }
}
