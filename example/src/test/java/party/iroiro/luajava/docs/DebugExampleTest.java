package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.lua55.Lua55;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.LuaValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class DebugExampleTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testJavaFunctionDebug() {
        for (Supplier<Lua> supplier : new Supplier[]{
                Lua51::new, Lua52::new, Lua53::new, Lua54::new, Lua55::new,
                LuaJ::new, LuaJit::new,
        }) {
            try (Lua L = supplier.get()) {
                L.openLibraries();
                List<LuaValue> trace = new ArrayList<>();
                L.register("hook", (K, args) -> {
                    String event = args[0].toString();
                    assertEquals("call", event);
                    if (trace.isEmpty()) {
                        for (int i = 0; i < 5; i++) {
                            LuaValue[] eval = K.eval("return debug.getinfo(" + i + ")");
                            if (eval.length == 1 && eval[0].type() != Lua.LuaType.NIL) {
                                trace.add(eval[0]);
                            } else {
                                K.newTable();
                                trace.add(K.get());
                            }
                        }
                    }
                    return null;
                });
                L.run("function test_func(i) return math.max(1, i) end");
                LuaValue[] eval = L.eval("debug.sethook(hook, 'c', 0); return test_func(10) + 10");
                assertEquals(20, eval[0].toInteger());
                L.run("debug.sethook()");

                Object[] names = trace.stream().map(v -> v.get("name").toString()).toArray();
                System.out.println(Arrays.toString(names));
                int index = L instanceof LuaJ ? 1 : (L instanceof LuaJit ? 2 : 3);
                assertEquals("test_func", names[index]);
            }
        }
    }

    @Test
    public void debugCountTest() {
        try (Lua L = new Lua52()) {
// #region debugCountTest
L.openLibraries();
int[] count = new int[1];
boolean[] insideTestFunc = new boolean[1];
L.register("hook", (K, args) -> {
    count[0]++;
    // Use debug.getinfo to get frame info
    insideTestFunc[0] |= K.eval("return debug.getinfo(3)")[0]
            .get("name").toString().equals("test_func");
    return null;
});
L.run("function test_func() return 42 end");
// Set hook
L.run("debug.sethook(hook, 'c', 1)");
L.run("test_func()");
L.run("debug.sethook()");
assertNotEquals(0, count[0]);
assertTrue(insideTestFunc[0]);
// #endregion debugCountTest
        }
    }
}
