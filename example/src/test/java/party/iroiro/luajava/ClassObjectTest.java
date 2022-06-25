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
            assertEquals(RUNTIME, L.run("t = java.require('java/lang/NoSystem')"));
            assertTrue(Objects.requireNonNull(L.toString(-1))
                    .contains("Unable to bind to class java/lang/NoSystem"));

            assertEquals(OK, L.run("t = java.require('party/iroiro/jua/ClassObjectTest')"));
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
