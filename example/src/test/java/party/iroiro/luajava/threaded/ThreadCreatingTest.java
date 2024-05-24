package party.iroiro.luajava.threaded;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

/**
 * Tests creating threads from the Lua side.
 */
public class ThreadCreatingTest {
    @SuppressWarnings("resource")
    @Test
    public void threadCreatingTestLua() {
        for (Lua L : new Lua[]{
                new Lua51(),
                new Lua52(),
                new Lua53(),
                new Lua54(),
                new LuaJit(),
                new LuaJ(),
        }) {
            testLuaCreatingThreads(L);
        }
    }

    public void testLuaCreatingThreads(Lua L) {
        try {
            L.openLibraries();
            L.setExternalLoader(new ClassPathLoader());
            Lua K = L.newThread();
            K.loadExternal("threads.threadCreating");
            while (true) {
                synchronized (K.getMainState()) {
                    if (!K.resume(0)) {
                        break;
                    }
                }
                //noinspection BusyWait
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            L.close();
        }
    }
}
