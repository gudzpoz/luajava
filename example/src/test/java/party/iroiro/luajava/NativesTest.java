package party.iroiro.luajava;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luaj.LuaJNatives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static party.iroiro.luajava.Lua.LuaType.USERDATA;

@Testable
public class NativesTest {
    @Test
    public void luaJNativesUserDataTest() {
        try (LuaJ L = new LuaJ()) {
            new LuaJNatives() {
                {
                    lua_newuserdata(L.getPointer(), new Object());
                }
            };
            assertEquals(USERDATA, L.type(-1));
        }
    }
}
