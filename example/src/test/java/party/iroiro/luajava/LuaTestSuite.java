package party.iroiro.luajava;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.Conversion.FULL;
import static party.iroiro.luajava.Lua.Conversion.SEMI;
import static party.iroiro.luajava.Lua.LuaType.*;

public class LuaTestSuite<T extends Lua> {
    public LuaTestSuite(T L) {
        this.L = L;
    }

    public void test() {
        L.openLibraries();
        testOverflow();
        testJavaToLuaConversions();
        testPushChecks();
        testTableOperations();
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
        assertThrows(NullPointerException.class, () -> L.push((JFunction) null));
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
                    new BigInteger("9999999999999999999999999999999999999" +
                            "99999999999999999999999999999999999999999999999"),
                    Long.MIN_VALUE, Long.MAX_VALUE
            },
            {
                    V(Object::equals),
                    USERDATA, STRING, STRING, "", "String"
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
                    System.out, Runtime.getRuntime(), new IllegalAccessError()
            },
    };

}
