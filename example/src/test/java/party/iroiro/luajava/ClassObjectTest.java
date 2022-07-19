package party.iroiro.luajava;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static party.iroiro.luajava.Lua.LuaError.OK;
import static party.iroiro.luajava.Lua.LuaError.RUNTIME;

public class ClassObjectTest {
    @Test
    public void classObjectTest() {
        try (Lua L = new Lua51()) {
            assertEquals(RUNTIME, L.run("t = java.import('java.lang.NoSystem')"));

            assertEquals(OK, L.run("t = java.import('party.iroiro.luajava.ClassObjectTest')"));
            L.getGlobal("t");
            assertEquals(Class.class, Objects.requireNonNull(L.toJavaObject(-1)).getClass());

            run.set(false);
            assertEquals(OK, L.run("t:classMethod(t)"));
            assertTrue(run.get());
        }
    }

    private static final AtomicBoolean run = new AtomicBoolean(false);

    public static void classMethod(Class<?> c) {
        if (c == ClassObjectTest.class) {
            run.set(true);
        }
    }
}
