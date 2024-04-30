package party.iroiro.luajava.threaded;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.interfaces.LuaTestSupplier;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests multiple threads with standalone Lua states.
 */
public class ThreadedTests {
    @Test
    public void threadedTests() throws InterruptedException {
        //noinspection rawtypes
        LuaTestSupplier[] constructors = new LuaTestSupplier[] {
                Lua51::new,
                Lua52::new,
                Lua53::new,
                Lua54::new,
                LuaJit::new,
                LuaJ::new,
        };
        //noinspection rawtypes
        for (LuaTestSupplier constructor : constructors) {
            AtomicInteger count = new AtomicInteger(0);
            ArrayList<Thread> threads = new ArrayList<>();
            final int cap = 50;
            threads.ensureCapacity(cap);
            for (int i = 0; i < cap; i++) {
                Thread thread = new Thread(() -> {
                    try (Lua L = (Lua) constructor.get()) {
                        L.set("sleep", (JFunction) L1 -> {
                            try {
                                count.incrementAndGet();
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                return L1.error(e);
                            }
                            return 0;
                        });
                        L.run("sleep()");
                    }
                });
                threads.add(thread);
                thread.start();
            }
            for (Thread t : threads) {
                t.join();
            }
            assertEquals(cap, count.get());
        }
    }
}
