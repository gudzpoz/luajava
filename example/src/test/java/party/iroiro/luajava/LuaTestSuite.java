package party.iroiro.luajava;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.Conversion.FULL;
import static party.iroiro.luajava.Lua.Conversion.SEMI;
import static party.iroiro.luajava.Lua.LuaError.*;
import static party.iroiro.luajava.Lua.LuaType.*;

public class LuaTestSuite<T extends Lua> {
    public LuaTestSuite(T L) {
        this.L = L;
    }

    public void test() {
        L.openLibraries();
        testOverflow();
        testJavaToLuaConversions();
        testLuaToJavaConversions();
        testPushChecks();
        testTableOperations();
        testMeasurements();
        testStackOperations();
        testRef();
        testRunners();
        testThreads();
        testProxy();
        testNotSupported();
        testOthers();
    }

    public static final AtomicInteger proxyIntegerTest = new AtomicInteger(0);
    private void testProxy() {
        L.push(true);
        assertNull(L.createProxy(new Class[0], Lua.Conversion.NONE));
        assertEquals(OK, L.run("proxyMap = { run = function()\n" +
                "java.import('party.iroiro.luajava.LuaTestSuite').proxyIntegerTest:set(-1024)\n" +
                "end" +
                "}"));
        L.getGlobal("proxyMap");
        Object proxy = L.createProxy(new Class[]{Runnable.class}, Lua.Conversion.NONE);
        proxyIntegerTest.set(0);
        ((Runnable) proxy).run();
        assertEquals(-1024, proxyIntegerTest.get());
    }

    private void testOthers() {
        L.openLibrary("math");
        assertEquals(OK, L.run("assert(1.0 == math.abs(-1.0))"));

        L.register("testOthersFunction", l -> {
            l.push("Hello");
            return 1;
        });
        assertEquals(OK, L.run("assert('Hello' == testOthersFunction())"));

        L.newRegisteredMetatable("myusertype");
        L.push(l -> {
            l.push(1);
            return 1;
        });
        L.setGlobal("userTypeFunc");
        /* Metamethods must be functions */
        L.run("function userTypeFuncWrap() return userTypeFunc() end");
        L.getGlobal("userTypeFuncWrap");
        L.setField(-2, "__index");
        L.pop(1);

        L.createTable(0, 0);
        L.getRegisteredMetatable("myusertype");
        L.setMetatable(-2);
        L.setGlobal("testOthersTable");
        assertEquals(OK, L.run("assert(1 == testOthersTable.a)"), () -> L.toString(-1));
        assertEquals(OK, L.run("assert(1 == testOthersTable.b)"), () -> L.toString(-1));
        assertEquals(OK, L.run("assert(1 == testOthersTable.whatever)"), () -> L.toString(-1));
        assertEquals(OK, L.run("assert(1 == testOthersTable.__index)"), () -> L.toString(-1));
        assertEquals(OK, L.run("assert(1 == testOthersTable[10])"), () -> L.toString(-1));

        AbstractLua lua = new AbstractLua(L.getLuaNative()) {
            @Override
            protected AbstractLua newThread(long L, int id, Lua mainThread) {
                return null;
            }

            @Override
            public LuaError convertError(int code) {
                return null;
            }

            @Override
            public LuaType convertType(int code) {
                return null;
            }
        };
        lua.push(1);
        assertNull(lua.toObject(-1));
        lua.close();
    }

    private void testThreads() {
        Lua sub = L.newThread();
        assertEquals(OK, sub.status());
        sub.getGlobal("print");
        sub.push(true);
        assertEquals(OK, sub.resume(1), () -> L.toString(-1));
        assertEquals(OK, sub.run("function threadCoroutineTest()\n" +
                "coroutine.yield(1)\n" +
                "coroutine.yield(2)\n" +
                "end"));
        sub.getGlobal("threadCoroutineTest");
        assertEquals(YIELD, sub.resume(0), () -> L.toString(-1));
        assertEquals(1.0, sub.toNumber(-1));
        sub.pop(1);
        assertEquals(YIELD, sub.resume(0), () -> L.toString(-1));
        assertEquals(2.0, sub.toNumber(-1));
        sub.pop(1);
        assertEquals(OK, sub.resume(0), () -> L.toString(-1));
        sub.close();

        integer.set(0);
        L.run("i = java.import('party.iroiro.luajava.LuaTestSuite').integer");
        L.getGlobal("i");
        assertEquals(OK, L.run("coroutine.resume(coroutine.create(\n" +
                "function() java.import('party.iroiro.luajava.LuaTestSuite').integer:set(1024) end\n" +
                "))"));
        assertEquals(1024, integer.get());
    }

    public static final AtomicInteger integer = new AtomicInteger(0);

    private void testRunners() {
        String s = "testRunnersString = 'Not OK'";
        assertEquals(OK, L.load(s));
        L.pop(1);
        byte[] bytes = s.getBytes();
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        assertFalse(wrap.isDirect());
        assertEquals(MEMORY, L.load(wrap, "notDirectBuffer"));
        assertEquals(MEMORY, L.run(wrap, "notDirectBuffer"));
        assertEquals(OK, L.run(getDirect(s), "directBuffer"));
        assertEquals(RUNTIME, L.run(getDirect("print("), "directBuffer"));
        L.getGlobal("testRunnersString");
        assertEquals("Not OK", L.toString(-1));
        L.pop(1);
        assertEquals(OK, L.load(getDirect(s), "directBuffer"));
        assertEquals(OK, L.pCall(0, 0));
    }

    private Buffer getDirect(String s) {
        byte[] bytes = s.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }

    private void testNotSupported() {
        assertThrows(UnsupportedOperationException.class, () -> L.yield(1));
        assertThrows(RuntimeException.class, () -> L.error(""));

    }

    private void testStackOperations() {
        Lua sub = L.newThread();
        int top = L.getTop();
        Enumeration<Object> objectEnumeration = Collections.emptyEnumeration();
        L.push(objectEnumeration, Lua.Conversion.NONE);
        L.xMove(sub, 1);
        assertSame(objectEnumeration, sub.toObject(-1));
        assertEquals(top, L.getTop());

        L.push(1);
        L.push(2);
        L.push(3);
        L.push(4);
        L.insert(-3);
        L.remove(-1);
        L.replace(-3);
        L.concat(2);
        assertEquals("24", L.toString(-1));

        L.push("1");
        L.push("2");
        L.concat(2);
        assertEquals("12", L.toString(-1));
        L.concat(0);
        assertEquals("", L.toString(-1));
        L.pop(3);
    }

    private void testMeasurements() {
        L.push(1);
        L.push(2);
        assertTrue(L.equal(-1, -1));
        assertTrue(L.rawEqual(-1, -1));
        assertTrue(L.lessThan(-2, -1));
        assertFalse(L.equal(-1, -2));
        assertFalse(L.rawEqual(-1, -2));
        assertFalse(L.lessThan(-1, -2));
        assertEquals(RUNTIME, L.run("a = 1 \n print(#a)"));
        L.push(Arrays.asList(1, 2, 3));
        assertEquals(3, L.length(-1));
        L.pop(3);
    }

    private void testLuaToJavaConversions() {
        L.pushNil();
        int start = L.getTop();
        L.push(true);
        L.getGlobal("print");
        L.push(1, Lua.Conversion.NONE);
        L.push(1);
        L.push("S");
        L.push(new HashMap<>(), FULL);
        L.newThread();
        L.push(l -> 0);
        int end = L.getTop();
        ArrayList<Function<Integer, Boolean>> assersions = new ArrayList<>(Arrays.asList(
                L::isNil, L::isNone, L::isNoneOrNil, L::isBoolean, L::isFunction, L::isJavaObject,
                L::isNumber, L::isString, L::isTable, L::isThread, L::isUserdata
        ));
        int[][] expected = {
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,}, // nil
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,}, // boolean
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,}, // function
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,}, // Java object
                {0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,}, // number (isstring == true)
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,}, // string
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,}, // table
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,}, // thread
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,}, // JFunction
        };
        Lua.LuaType[] types = {NIL, BOOLEAN, FUNCTION, USERDATA, NUMBER, STRING, TABLE, THREAD, FUNCTION};
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < assersions.size(); j++) {
                assertEquals(1 == expected[i - start][j], assersions.get(j).apply(i),
                        "Testing (" + (i - start) + ", " + j + ")");
                assertEquals(types[i - start], L.type(i));
            }
        }
        for (int i = 1; i < 1000; i++) {
            assertTrue(L.isNone(i + end));
            assertTrue(L.isNoneOrNil(i + end));
            assertEquals(NONE, L.type(i + end));
        }
        L.pop(expected.length);

        LuaNative luaNative = L.getLuaNative();

        luaNative.lua_pushlightuserdata(L.getPointer(), 0);
        assertEquals(LIGHTUSERDATA, L.type(-1));
        assertNull(L.toObject(-1));
        assertNull(L.toObject(-1, Void.class));
        assertNull(L.toObject(-1, Integer.class));
        L.pop(1);

        HashMap<Object, Object> map = new HashMap<>();
        L.push(map, Lua.Conversion.NONE);
        assertSame(map, L.toObject(-1, Map.class));
        assertNull(L.toObject(-1, List.class));
        L.pop(1);

        L.push(new BigInteger("127"), Lua.Conversion.NONE);
        ArrayList<Class<?>> classes = new ArrayList<>(Arrays.asList(
                byte.class, Byte.TYPE, Byte.class,
                short.class, Short.TYPE, Short.class,
                int.class, Integer.TYPE, Integer.class,
                long.class, Long.TYPE, Long.class,
                float.class, Float.TYPE, Float.class,
                double.class, Double.TYPE, Double.class
        ));
        for (int i = 0; i < classes.size(); i += 3) {
            for (int j = 0; j < 3; j++) {
                Object o = L.toObject(-1, classes.get(i + j));
                assertInstanceOf(classes.get(i + 2), o);
                assertInstanceOf(Number.class, o);
                assertEquals(127.0, ((Number) o).doubleValue());
            }
        }
        assertNull(L.toObject(-1, BigDecimal.class));
        L.pop(1);

        testToMap(luaNative);

        testToList();
    }

    private void testToList() {
        List<Object> l = Collections.emptyList();
        L.push(l, Lua.Conversion.NONE);
        assertSame(l, L.toList(-1));
        L.push(true);
        assertNull(L.toList(-1));
        assertEquals(OK, L.run("testToListList = {1, 2, 3, 4, 5}"));
        L.getGlobal("testToListList");
        assertIterableEquals(Arrays.asList(1., 2., 3., 4., 5.), L.toList(-1));
        L.pop(3);
    }

    private void testToMap(LuaNative luaNative) {
        L.push(true);
        assertNull(L.toMap(-1));
        HashMap<Object, Object> emptyMap = new HashMap<>();
        L.push(emptyMap, Lua.Conversion.NONE);
        assertSame(emptyMap, L.toMap(-1));
        L.createTable(0, 1);
        luaNative.lua_pushlightuserdata(L.getPointer(), 0);
        luaNative.lua_pushlightuserdata(L.getPointer(), 1);
        L.setTable(-3);
        luaNative.lua_pushlightuserdata(L.getPointer(), 2);
        L.push(true);
        L.setTable(-3);
        L.push(false);
        luaNative.lua_pushlightuserdata(L.getPointer(), 3);
        L.setTable(-3);
        assertTrue(Objects.requireNonNull(L.toMap(-1)).isEmpty());
        L.pop(3);
    }

    private void testRef() {
        L.createTable(0, 0);
        testRef(o -> {
            L.push(o, Lua.Conversion.NONE);
            return L.ref(-2);
        }, i -> {
            L.rawGetI(-1, i);
            Object o = L.toJavaObject(-1);
            L.pop(1);
            return o;
        }, i -> L.unRef(-1, i));
        L.pop(1);
        testRef(o -> {
            L.push(o, Lua.Conversion.NONE);
            return L.ref();
        }, i -> {
            L.refGet(i);
            Object o = L.toJavaObject(-1);
            L.pop(1);
            return o;
        }, L::unref);
    }

    private void testRef(Function<Object, Integer> ref,
                         Function<Integer, Object> refGet,
                         Consumer<Integer> unref) {
        L.createTable(0, 0);
        int nilRef = ref.apply(null);
        assertEquals(nilRef, ref.apply(null));
        HashMap<Integer, Integer> values = new HashMap<>();
        for (int i = 1000; i > 0; i--) {
            Integer j = i;
            assertNull(values.put(ref.apply(j), j));
        }
        assertEquals(1000, values.size());
        for (Map.Entry<Integer, Integer> entry : values.entrySet()) {
            assertEquals(refGet.apply(entry.getKey()), entry.getValue());
            unref.accept(entry.getKey());
        }
        for (Map.Entry<Integer, Integer> entry : values.entrySet()) {
            assertNull(refGet.apply(entry.getKey()));
        }
    }

    private void testTableOperations() {
        L.createTable(1000, 1000);
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            String v = UUID.randomUUID().toString();
            L.push(v);
            switch (i % 3) {
                case 0:
                    L.pushValue(-1);
                    L.setTable(-3);
                    break;
                case 1:
                    L.setField(-2, v);
                    break;
                case 2:
                    L.pushValue(-1);
                    L.rawSet(-3);
                    break;
            }
            values.add(v);
        }
        int i = 0;
        for (String v : values) {
            switch (i % 3) {
                case 0:
                    L.push(v);
                    L.getTable(-2);
                    break;
                case 1:
                    L.getField(-1, v);
                    break;
                case 2:
                    L.push(v);
                    L.rawGet(-2);
                    break;
            }
            assertEquals(v, L.toString(-1));
            L.pop(1);
            i++;
        }
        L.pushNil();
        while (L.next(-2) != 0) {
            assertTrue(L.equal(-2, -1), L.toString(-1));
            String v = L.toString(-1);
            assertTrue(values.contains(v));
            values.remove(v);
            L.pop(1);
        }
        assertEquals(0, values.size());
        for (int j = Integer.MAX_VALUE - 2000; j < Integer.MAX_VALUE; j++) {
            L.push(Integer.MAX_VALUE - j);
            L.rawSetI(-2, j);
        }
        for (int j = Integer.MAX_VALUE - 2000; j < Integer.MAX_VALUE; j++) {
            L.rawGetI(-1, j);
            assertEquals((double) Integer.MAX_VALUE - j, L.toNumber(-1));
            L.pop(1);
        }
        L.pop(1);
    }

    @SuppressWarnings("ConstantConditions")
    private void testPushChecks() {
        assertThrows(IllegalArgumentException.class, () -> L.pushArray(1));
        assertThrows(IllegalArgumentException.class, () -> L.pushJavaArray(1));
        assertThrows(IllegalArgumentException.class, () -> L.pushJavaObject(new int[0]));
        assertThrows(NullPointerException.class, () -> L.pushArray(null));
        assertThrows(NullPointerException.class, () -> L.pushJavaArray(null));
        assertThrows(NullPointerException.class, () -> L.pushJavaObject(null));
        // assertThrows(NullPointerException.class, () -> L.push((JFunction) null));
    }

    private void testOverflow() {
        L.createTable(1, 1);
        int testTableI = L.getTop();
        L.push("S");
        L.setField(-2, "S");
        L.push("I");
        L.rawSetI(-2, 1);
        L.createTable(0, 1);
        L.push("F");
        L.setField(-2, "F");
        L.setMetatable(-2);
        L.pushValue(-1);
        int ref = L.ref();
        ArrayList<Consumer<T>> stackIncrementingOperations = new ArrayList<>(Arrays.asList(
                L -> L.createTable(0, 0),
                L -> L.getGlobal("java"),
                L -> L.refGet(ref),
                L -> L.pushValue(testTableI),
                L -> L.getMetatable(testTableI),
                L -> L.getField(testTableI, "S"),
                L -> L.rawGetI(testTableI, 1),
                L -> L.getMetaField(testTableI, "F")
        ));
        for (Object[] data : DATA) {
            for (Lua.Conversion conv : Lua.Conversion.values()) {
                stackIncrementingOperations.add(L -> L.push(data[4], conv));
            }
        }
        for (Consumer<T> t : stackIncrementingOperations) {
            assertThrows(RuntimeException.class, () -> {
                double i = 1.0;
                //noinspection InfiniteLoopStatement
                while (true) {
                    L.checkStack((int) i);
                    t.accept(L);
                    i *= 1.0001;
                }
            }, "No more stack space available");
            L.setTop(testTableI);
        }
        L.pop(1);
    }

    private void testJavaToLuaConversions() {
        L.push(l -> {
            l.push(1024);
            return 1;
        });
        L.setGlobal("l2jConvTest");
        assertEquals(OK, L.run("assert(1024 == l2jConvTest())"));

        for (Object[] data : DATA) {
            Lua.Conversion[] conversions = {Lua.Conversion.NONE, SEMI, FULL};
            for (int i = 0; i < conversions.length; i++) {
                for (int j = 4; j < data.length; j++) {
                    Object o = data[j];
                    Object expected = data[i + 1];
                    L.push(o, conversions[i]);
                    assertInstanceOf(Verifier.class, data[0]);
                    ((Verifier) data[0]).verify(L, o);
                    if (expected instanceof Lua.LuaType) {
                        assertEquals(expected, L.type(-1), "Testing " + o);
                    } else if (expected instanceof Class) {
                        assertEquals(USERDATA, L.type(-1), "Testing " + o);
                        assertTrue(L.isJavaObject(-1), "Testing " + o);
                        Object actualValue = L.toJavaObject(-1);
                        assertInstanceOf(((Class<?>) expected), actualValue, "Testing " + o);
                        assertEquals(o, actualValue, "Testing " + o);
                    } else {
                        fail("Test data incorrect");
                    }
                    L.pop(1);
                }
            }
        }
    }

    private static class Verifier {
        private final BiPredicate<Object, Object> verifier;

        public Verifier(BiPredicate<Object, Object> verifier) {
            this.verifier = verifier;
        }

        public void verify(Lua L, Object original) {
            assertTrue(verifier.test(original, L.toObject(-1)),
                    original == null ? "null" : original.getClass().getName());
        }
    }

    public static Verifier V(BiPredicate<Object, Object> verifier) {
        return new Verifier(verifier);
    }

    private final T L;
    private static final Object[][] DATA = {
            /* { Verifier,
             *   NONE_CONVERTED_TYPE,
             *   SEMI_CONVERTED_TYPE,
             *   FULL_CONVERTED_TYPE,
             *   testValue1, testValue2, ... }
             */
            {
                    V((o, l) -> l == null),
                    NIL, NIL, NIL, null
            },
            {
                    V(Object::equals),
                    USERDATA, BOOLEAN, BOOLEAN,
                    true, false
            },
            {
                    V((o, i) -> {
                        if (o.equals(i)) {
                            return true;
                        }
                        if (i instanceof Number) {
                            if (o instanceof BigInteger) {
                                return !o.equals(i)
                                        && ((BigInteger) o).compareTo(
                                        BigInteger.valueOf(((Number) i).longValue())) > 0;
                            } else if (o instanceof Number) {
                                return Math.abs(((Number) o).doubleValue() - ((Number) i).doubleValue())
                                        < 0.00000001;
                            } else if (o instanceof Character) {
                                return ((Number) i).intValue() == (int) ((Character) o);
                            }
                        }
                        return false;
                    }),
                    USERDATA, NUMBER, NUMBER,
                    'c', (byte) 1, (short) 2, 3, 4L, 1.2, 2.3, 4.5f,
                    Long.MIN_VALUE, Long.MAX_VALUE
            },
            {
                    V(Object::equals),
                    USERDATA, STRING, STRING, "", "String"
            },
            {
                    V(((o, o2) -> o == null || o2 == null || o.equals(o2))),
                    USERDATA, FUNCTION, FUNCTION,
                    (JFunction) l -> 0, (JFunction) l -> 1,
            },
            {
                    V((o, list) -> {
                        if (o.getClass().isArray()) {
                            if (list instanceof Map) {
                                Map<?, ?> l = (Map<?, ?>) list;
                                return Array.getLength(o) == l.size();
                            } else if (list.getClass().isArray()) {
                                return Array.getLength(o) == Array.getLength(list);
                            }
                        }
                        return false;
                    }),
                    USERDATA, USERDATA, TABLE,
                    new int[]{}, new int[]{1, 2, 3, 4, 5, 6},
                    new String[]{"", "String"}
            },
            {
                    V((o, m) -> {
                        if (o instanceof Collection) {
                            if (m instanceof Collection) {
                                return ((Collection<?>) o).size() == ((Collection<?>) m).size();
                            } else if (m instanceof Map) {
                                return ((Collection<?>) o).size() == ((Map<?, ?>) m).size();
                            }
                        }
                        return false;
                    }),
                    USERDATA, USERDATA, TABLE,
                    Collections.synchronizedCollection(Collections.emptyList()),
                    Collections.singleton("A"),
                    Collections.singleton(Collections.singletonList("B")),
                    Arrays.asList(1, 2, 3, 4, 5)
            },
            {
                    V((o, m) -> {
                        if (o instanceof Map && m instanceof Map) {
                            return ((Map<?, ?>) o).size() == ((Map<?, ?>) m).size();
                        }
                        return false;
                    }),
                    USERDATA, USERDATA, TABLE,
                    Collections.emptyMap(),
                    Collections.singletonMap("A", "B"),
                    new HashMap<ArrayList<String>, String>()
            },
            {
                    V(Object::equals),
                    USERDATA, USERDATA, USERDATA,
                    Class.class, Integer.class
            },
            {
                    V(Object::equals),
                    USERDATA, USERDATA, USERDATA,
                    new BigInteger("9999999999999999999999999999999999999" +
                            "99999999999999999999999999999999999999999999999"),
                    new AtomicInteger(1),
                    System.out, Runtime.getRuntime(), new IllegalAccessError()
            },
    };

}
