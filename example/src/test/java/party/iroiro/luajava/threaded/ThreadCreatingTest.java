package party.iroiro.luajava.threaded;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.luaj.LuaJ;

public class ThreadCreatingTest {
    @Test
    public void threadCreatingTest() {
        try (Lua L = new Lua51()) {
            L.setExternalLoader(new ClassPathLoader());
            L.loadExternal("threads.threadCreating");
            synchronized (L.getMainState()) {
                L.pCall(0, Consts.LUA_MULTRET);
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void threadCreatingTestJ() {
        try (Lua L = new LuaJ()) {
            L.setExternalLoader(new ClassPathLoader());
            L.loadExternal("threads.threadCreating");
            synchronized (L.getMainState()) {
                L.pCall(0, Consts.LUA_MULTRET);
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
