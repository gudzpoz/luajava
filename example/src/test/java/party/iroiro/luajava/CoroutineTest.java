package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testable
public class CoroutineTest {
    @Test
    public void testCoroutine() {
        //noinspection resource
        for (Lua L : new Lua[] {
                new Lua51(),
                new Lua52(),
                new Lua53(),
                new Lua54(),
                new LuaJit(),
                new LuaJ(),
        }) {
            coroutineTest(L);
        }
    }

    private void coroutineTest(Lua L) {
        L.openLibraries();
        L.setExternalLoader(new ClassPathLoader());
        L.loadExternal("tests.coTest");
        L.pCall(0, Consts.LUA_MULTRET);
        Lua coL = L.newThread();
        int ignored = L.ref();
        coL.getGlobal("main");
        int i = 1, j = 1;
        for (int l = 0; l < 36; l++) {
            assertTrue(coL.resume(0));
            assertEquals(i, coL.toNumber(-1));
            coL.pop(1);
            int k = i + j;
            i = j;
            j = k;
        }
        L.close();
    }
}
