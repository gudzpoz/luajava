package party.iroiro.luajava.threaded;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.ResourceLoader;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.luaj.LuaJ;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreadCreatingTest {
    @Test
    public void threadCreatingTest() throws IOException {
        ResourceLoader loader = new ResourceLoader();
        try (Lua L = new Lua51()) {
            assertEquals(0, loader.load("/threads/threadCreating.lua", L));
            synchronized (L.getMainState()) {
                L.pCall(0, Consts.LUA_MULTRET);
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void threadCreatingTestJ() throws IOException {
        ResourceLoader loader = new ResourceLoader();
        try (Lua L = new LuaJ()) {
            assertEquals(0, loader.load("/threads/threadCreating.lua", L));
            synchronized (L.getMainState()) {
                L.pCall(0, Consts.LUA_MULTRET);
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
