package party.iroiro.luajava.value;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;
import static party.iroiro.luajava.Lua.LuaType.*;

public class LuaValueSuite<T extends Lua> {
    private final T L;

    public LuaValueSuite(T L) {
        this.L = L;
    }

    public void test() {
        equalityTest(L, true);
        equalityTest(L.newThread(), true);
        try (Lua K = new Lua51()) {
            equalityTest(K, false);
        }
        differentThreadTest();
        tableTest();
        nilTest();
        callTest();
        luaStateTest();
    }

    private void luaStateTest() {
        try (LuaValue value = L.get("java")) {
            assertEquals(TABLE, value.type());
        }
        assertNull(L.execute("("));
    }

    private void callTest() {
        L.push((l) -> {
            int top = l.getTop();
            int sum = 0;
            for (int i = 1; i <= top; i++) {
                sum += (int) l.toNumber(i);
            }
            l.push(sum);
            return 1;
        });
        LuaValue func = L.get();
        LuaValue[] results = func.call(1, "2", 3., L.from(4), L.from("5"));
        assertEquals(1, results.length);
        assertEquals(NUMBER, results[0].type());
        assertEquals(15., results[0].toJavaObject());
        L.push((l) -> {
            l.push("Some error");
            return -1;
        });
        try (LuaValue func2 = L.get()) {
            assertNull(func2.call());
        }
    }

    private void nilTest() {
        L.pushNil();
        LuaValue value = L.get();
        assertEquals(NIL, value.type());
        L.pushNil();
        value.push();
        assertTrue(L.equal(-1, -2));
        L.pop(2);
    }

    private void tableTest() {
        int top = L.getTop();
        LuaValue[] values = L.execute("return {1, 2, 3, a = 'b', c = 'd'}, 1024");
        assertEquals(2, values.length);
        LuaValue value = values[0];

        Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(value.toJavaObject());
        assertEquals(5, map.size());
        assertEquals(1., map.get(1.));
        assertEquals(2., map.get(2.));
        assertEquals(3., map.get(3.));
        assertEquals("b", map.get("a"));
        assertEquals("d", map.get("c"));

        assertEquals(top, L.getTop());
        assertEquals(1., value.get(1).toJavaObject());
        assertEquals(2., value.get(2).toJavaObject());
        assertEquals(3., value.get(3).toJavaObject());
        assertEquals("b", value.get("a").toJavaObject());
        assertEquals("d", value.get(L.from("c")).toJavaObject());
        value = L.execute("return {}")[0];
        value.set(1, L.from(1));
        value.set(2, L.from(2));
        value.set(L.from(3), L.from(3));
        value.set("a", L.from("b"));
        value.set(L.from("c"), L.from("d"));
        value.set(L.from(true), L.from(false));
        assertEquals(1., value.get(1).toJavaObject());
        assertEquals(2., value.get(2).toJavaObject());
        assertEquals(3., value.get(3).toJavaObject());
        assertEquals("b", value.get("a").toJavaObject());
        assertEquals("d", value.get(L.from("c")).toJavaObject());
        assertEquals(false, value.get(L.from(true)).toJavaObject());
        assertNotEquals(L.from(1), value);

        assertEquals(top, L.getTop());
    }

    private void differentThreadTest() {
        try (Lua K = new Lua51()) {
            L.pushNil();
            K.pushNil();
            assertThrows(IllegalArgumentException.class, () -> L.get().get(K.get()));
            K.pushNil();
            assertThrows(UnsupportedOperationException.class, () -> K.get().push(L));

            K.pushNil();
            LuaValue nil = K.get();
            L.push(nil, Lua.Conversion.NONE);
            assertTrue(L.isUserdata(-1));
            L.pop(1);
            Lua J = L.newThread();
            J.pushNil();
            L.push(J.get(), Lua.Conversion.NONE);
            assertTrue(L.isNil(-1));
            L.push(L.get(), Lua.Conversion.NONE);
            assertTrue(L.isNil(-1));
            L.pop(1);
        }
        L.createTable(0, 0);
        try (LuaValue value = L.get()) {
            value.push(L);
            value.push();
            assertTrue(L.equal(-1, -2));
            L.pop(2);
        }
    }

    private void equalityTest(Lua K, boolean equals) {
        int top = L.getTop();
        L.pushNil();
        assertEquals(equals, ImmutableLuaValue.NIL(K).equals(L.get()));
        L.push(true);
        assertEquals(equals, ImmutableLuaValue.TRUE(K).equals(L.get()));
        L.push(false);
        assertEquals(equals, ImmutableLuaValue.FALSE(K).equals(L.get()));
        L.push(100);
        assertEquals(equals, ImmutableLuaValue.NUMBER(K, 100).equals(L.get()));
        L.push("string content");
        assertEquals(equals, ImmutableLuaValue.STRING(K, "string content").equals(L.get()));

        assertNotEquals(K.from(1), L.from(false));
        assertNotEquals(L.from(1), L.from(2));
        assertNotEquals(K.from(1), new Object());

        AbstractLuaValue<Lua> mock = new AbstractLuaValue<Lua>(L, NUMBER) {
            @Override
            public void push() {

            }

            @Override
            public Object toJavaObject() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public Lua.LuaType type() {
                return NUMBER;
            }
        };
        assertNotEquals(L.from(1), mock);

        L.push(Collections.emptyList());
        K.push(Collections.emptyList());
        try (LuaValue l = L.get(); LuaValue k = K.get()) {
            l.push();
            try (LuaValue j = L.get()) {
                assertEquals(l, j);
                assertNotEquals(j, k);
                assertNotEquals(l, L.from(1));
                AbstractLuaValue<Lua> mock1 = new AbstractLuaValue<Lua>(L, TABLE) {
                    @Override
                    public void push() {

                    }

                    @Override
                    public Object toJavaObject() {
                        return null;
                    }

                    @Override
                    public void close() {

                    }

                    @Override
                    public Lua.LuaType type() {
                        return TABLE;
                    }
                };
                assertNotEquals(l, mock1);
                //noinspection EqualsWithItself
                assertEquals(l, l);
            }
        }

        assertEquals(top, L.getTop());
    }
}
