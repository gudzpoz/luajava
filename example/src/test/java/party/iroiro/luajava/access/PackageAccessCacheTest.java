package party.iroiro.luajava.access;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua51.Lua51;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageAccessCacheTest {
    public interface TestInterface {
        int test();
    }

    public static abstract class TestAbs implements TestInterface {
        public int test(int a) {
            return a;
        }
    }

    static abstract class TestAbs2 extends TestAbs {
        @Override
        public int test() {
            return test(0, 0, 42);
        }

        @SuppressWarnings("SameParameterValue")
        int test(int a, int b, int c) {
            return a + b + c;
        }
    }

    public static final class TestClass extends TestAbs2 {
        public int test(int a, int b) {
            return a + b;
        }
    }

    @Test
    void testNonPublicAccess() {
        try (Lua L = new Lua51()) {
            L.openLibraries();
            L.set("test", new TestClass());
            assertEquals(42, L.eval("return test:test()")[0].toInteger());
            assertEquals(42, L.eval("return test:test(42)")[0].toInteger());
            assertEquals(6, L.eval("return test:test(2, 4)")[0].toInteger());

            assertThrows(LuaException.class, () -> L.eval("return test:test(2, 4, 6)"));
        }
    }
}
