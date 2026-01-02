package party.iroiro.luajava.access;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackageAccessTest {
    public interface TestInterface {
        int test();
    }

    static final class TestClass implements TestInterface {
        @Override
        public int test() {
            return 42;
        }
    }

    @Test
    void testNonPublicAccess() {
        try (Lua L = new Lua51()) {
            L.openLibraries();
            L.set("test", new TestClass());
            assertEquals(42, L.eval("return test:test()")[0].toInteger());
        }
    }
}
