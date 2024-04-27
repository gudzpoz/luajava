package party.iroiro.luajava.luaj;

import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaError.OK;
import static party.iroiro.luajava.Lua.LuaType.TABLE;

public class LuaJTest {
    @Test
    public void testLuaJGlobalNop() {
        try (Lua L = new LuaJ()) {
            L.getLuaNative().loadAsGlobal();
        }
    }

    @Test
    public void testLuaJConcat() {
        try (Lua L = new LuaJ()) {
            L.createTable(0, 0);
            assertThrows(IllegalArgumentException.class, () -> L.concat(-1));
            L.concat(1);
            assertEquals(TABLE, L.type(-1));
        }
    }

    @Test
    public void testLuaJUnsupported() {
        try (Lua L = new LuaJ()) {
            LuaJNatives natives = (LuaJNatives) L.getLuaNative();
            long p = L.getPointer();
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_topointer(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_tothread(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_touserdata(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_yield(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_gethookcount(p));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_gethookmask(p));

            natives.luaL_where(p, 0);
            assertEquals("", L.toString(-1));
        }
    }

    @Test
    public void testLuaJTypes() {
        try (Lua L = new LuaJ()) {
            LuaJNatives natives = (LuaJNatives) L.getLuaNative();
            L.push((J) -> 0);
            natives.lua_pushlightuserdata(L.getPointer(), 1000);
            assertEquals(1, natives.lua_islightuserdata(L.getPointer(), -1));
            assertEquals(0, natives.lua_islightuserdata(L.getPointer(), -2));

            assertEquals(1, natives.lua_iscfunction(L.getPointer(), -2));
            assertEquals(0, natives.lua_iscfunction(L.getPointer(), -1));

            natives.lua_newtable(L.getPointer());
            assertEquals(TABLE, L.type(-1));
        }
    }

    @Test
    public void testLuaJMisc() {
        try (Lua L = new LuaJ()) {
            LuaJNatives natives = (LuaJNatives) L.getLuaNative();
            long p = L.getPointer();
            assertEquals("BBB", natives.luaL_gsub(p, "AAA", "A", "B"));

            assertEquals(OK, L.run("local a = 'up'; return function() return a end"));
            assertEquals("", natives.lua_getupvalue(p, -1, 0));
            assertEquals("up", L.toString(-1));
            L.pop(1);
            L.push("down");
            assertEquals("", natives.lua_setupvalue(p, -2, 0));
            assertEquals(OK, L.pCall(0, 1));
            assertEquals("down", L.toString(-1));

            assertEquals(OK, L.run("local t = {}; setmetatable(t, { a = function() return 100 end }); return t"));
            assertEquals(1, natives.luaL_callmeta(p, -1, "a"));
            assertEquals(100, L.toNumber(-1));
        }
    }
}
