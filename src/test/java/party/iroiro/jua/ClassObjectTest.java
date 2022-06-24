package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassObjectTest {
    @Test
    public void classObjectTest() {
        try (Lua L = new Lua51()) {
            assertEquals(1, L.run("t = java.require('java/lang/NoSystem')"));
            assertTrue(L.toString(-1).contains("Unable to bind to class java/lang/NoSystem"));

            assertEquals(0, L.run("t = java.require('party/iroiro/jua/ClassObjectTest')"));
            L.getGlobal("t");
            assertEquals(Class.class, L.toJavaObject(-1).getClass());

            run.set(false);
            assertEquals(0, L.run("t:classMethod(t)"));
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
