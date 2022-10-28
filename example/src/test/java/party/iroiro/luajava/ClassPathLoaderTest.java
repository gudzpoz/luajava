package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.lua52.Lua52;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luajit.LuaJit;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaError.OK;

public class ClassPathLoaderTest {
    @Test
    public void classPathLoaderTest() {
        ClassPathLoader loader = new ClassPathLoader();
        ArrayList<Lua> luas = new ArrayList<>(Arrays.asList(
                new Lua51(),
                new Lua52(),
                new Lua53(),
                new Lua54(),
                new LuaJit()
        ));
        for (Lua L : luas) {
            assertNull(loader.load("a.module.nowhere.to.be.found", L));
            Buffer buffer = loader.load("suite.importTest", L);
            assertNotNull(buffer);
            assertTrue(buffer.isDirect());
            assertEquals(0, buffer.position());
            assertNotEquals(0, buffer.limit());
            LuaScriptSuite.addAssertThrows(L);
            assertEquals(OK, L.load(buffer, "suite.importTest"));
            assertEquals(OK, L.pCall(0, Consts.LUA_MULTRET), L.toString(-1));

            ByteBuffer b = ByteBuffer.allocate(3);
            ClassPathLoader.BufferOutputStream out = new ClassPathLoader.BufferOutputStream(b);
            out.write(1);
            out.write(new byte[] {2, 3}, 0, 2);
            b.flip();
            assertEquals(1, b.get());
            assertEquals(2, b.get());
            assertEquals(3, b.get());
            assertDoesNotThrow(out::close);

            L.close();
        }
    }
}
