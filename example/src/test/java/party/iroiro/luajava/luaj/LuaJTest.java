package party.iroiro.luajava.luaj;

import org.junit.jupiter.api.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import party.iroiro.luajava.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaType.TABLE;
import static party.iroiro.luajava.Lua.LuaType.USERDATA;
import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

public class LuaJTest {
    private void assertLuaThrows(Lua L, String code, String message) {
        LuaException e = assertThrows(LuaException.class, () -> L.run(code));
        assertTrue(e.getMessage().contains(message), e.getMessage());
    }

    @Test
    public void testLuaJGlobalNop() {
        try (Lua L = new LuaJ()) {
            L.getLuaNatives().loadAsGlobal();
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
            LuaJNatives natives = (LuaJNatives) L.getLuaNatives();
            long p = L.getPointer();
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_topointer(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_tothread(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_touserdata(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_yield(p, 1));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_gethookcount(p));
            assertThrows(UnsupportedOperationException.class, () -> natives.lua_gethookmask(p));

            natives.luaL_where(p, 0);
            assertEquals("", L.toString(-1));

            assertLuaThrows(L, "java.array(1)",
                    "bad argument #1 to 'java.array': __jclass__ or __jobject__ expected");
            assertLuaThrows(L, "java.unwrap(1)", "bad argument #1 to java.unwrap");
            L.run("s = java.import('java.lang.String')('new string')");
            assertLuaThrows(L, "java.new(1)", "bad argument #1 to 'java.new': __jclass__ or __jobject__ expected");
            assertLuaThrows(L, "java.new(s)", "bad argument #1 to 'java.new'");

            L.pushThread();
            L.setGlobal("thread");
            assertLuaThrows(L, "java.detach(thread)", "unable to detach a main state");
        }
    }

    @Test
    public void testLuaJState() {
        LuaJState L = new LuaJState(0, 0, new Globals(), null, null);
        L.push(LuaValue.valueOf(""));
        LuaValue v = L.toLuaValue(-1);
        assertDoesNotThrow(() -> {
            L.insert(LuaJConsts.LUA_REGISTRYINDEX, v);
            L.insert(0, v);
            L.insert(100, v);
            L.insert(2, v);
            assertEquals(2, L.getTop());
        });
        assertDoesNotThrow(() -> {
            L.remove(LuaJConsts.LUA_REGISTRYINDEX);
            L.remove(0);
            L.remove(100);
            assertEquals(2, L.getTop());
        });
        assertDoesNotThrow(() -> {
            L.replace(LuaJConsts.LUA_REGISTRYINDEX, v);
            L.replace(0, v);
            L.replace(100, v);
            assertEquals(2, L.getTop());
        });

        L.popFrame();
        assertThrows(IllegalStateException.class, L::stack);
        assertNull(LuaJState.unwrapLuaError(null));
    }

    @Test
    public void memoryErrorCodeTest() {
        try (LuaJ L = new LuaJ()) {
            assertEquals(LuaException.LuaError.MEMORY, L.convertError(LuaJConsts.LUA_ERRMEM));
        }
    }

    @Test
    public void testLuaJNatives() {
        try (LuaJ L = new LuaJ()) {
            LuaJNatives C = (LuaJNatives) L.getLuaNatives();
            long ptr = L.getPointer();
            assertThrows(UnsupportedOperationException.class, () -> C.lua_error(ptr));

            Lua K = L.newThread();
            K.pushThread();
            assertTrue(K.isThread(-1));

            L.push(1);
            L.push(2);
            assertEquals(1, C.luaJ_compare(ptr, -2, -1, 1));

            assertThrowsLua(LuaException.LuaError.RUNTIME, () -> C.luaJ_resume(ptr, 0),
                    "resuming the main thread is not supported with luaj");

            assertEquals(LuaJConsts.LUA_ERRSYNTAX, C.luaL_dostring(ptr, "("));

            L.createTable(0, 0);
            assertEquals(0, C.luaL_callmeta(ptr, -1, "__eq"));
            L.createTable(0, 0);
            L.setMetatable(-2);
            assertEquals(0, C.luaL_callmeta(ptr, -1, "__eq"));

            assertEquals(0, C.lua_status(ptr));

            L.run("return function() return 'message' end");
            L.run("return function() assert(false) end");
            C.lua_pcall(ptr, 0, 0, -2);
            assertEquals("message", L.toString(-1));
            L.run("return function() assert(false) end");
            L.run("return function() local t = nil; return t.msg end");
            C.lua_pcall(ptr, 0, 0, -2);
            assertTrue(Objects.requireNonNull(L.toString(-1)).contains("assertion failed"));

            L.run("return function() return 3 end");
            C.lua_pcall(ptr, 0, 3, 0);
            assertTrue(L.isNil(-1));
            assertTrue(L.isNil(-2));
            assertTrue(L.isNumber(-3));
            assertEquals(3, L.toInteger(-3));
        }
    }

    @Test
    public void testLuaJTypes() {
        try (Lua L = new LuaJ()) {
            LuaJNatives natives = (LuaJNatives) L.getLuaNatives();
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
            LuaJNatives natives = (LuaJNatives) L.getLuaNatives();
            long p = L.getPointer();

            L.run("local a = 'up'; return function() return a end");
            assertEquals("", natives.lua_getupvalue(p, -1, 0));
            assertEquals("up", L.toString(-1));
            L.pop(1);
            L.push("down");
            assertEquals("", natives.lua_setupvalue(p, -2, 0));
            L.pCall(0, 1);
            assertEquals("down", L.toString(-1));

            L.run("local t = {}; setmetatable(t, { a = function() return 100 end }); return t");
            assertEquals(1, natives.luaL_callmeta(p, -1, "a"));
            assertEquals(100, L.toNumber(-1));
        }
    }

    @Test
    public void testLuaJCoroutine() {
        try (Lua L = new LuaJ()) {
            synchronized (L.getMainState()) {
                L.openLibraries();
                L.setExternalLoader(new ClassPathLoader());
                L.loadExternal("threads.luaJCoroutineTest");
                L.pCall(0, Consts.LUA_MULTRET);
            }
        }
    }

    @Test
    public void luaJNativesUserDataTest() {
        try (LuaJ L = new LuaJ()) {
            Object object = new Object();
            LuaJNatives C = (LuaJNatives) L.getLuaNatives();
            C.lua_newuserdata(L.getPointer(), object);
            assertEquals(USERDATA, L.type(-1));
            L.setGlobal("customObject");
            L.set("object", object);
            L.run("assert(object ~= customObject)");
        }
    }

    @Test
    public void testMath() {
        try (LuaJ L = new LuaJ()) {
            L.openLibraries();
            double eLog = L.eval("return math.log(" + Math.E + ")")[0].toNumber();
            assertEquals(1., eLog, 0.000001);
            double tenLog = L.eval("return math.log(100, 10)")[0].toNumber();
            assertEquals(2., tenLog, 0.000001);
        }
    }

    @Test
    public void caughtError() {
        try (LuaJ L = new LuaJ()) {
            L.run("assert(not java.catched())");
            L.set("throws", (JFunction) (J) -> J.error(new Exception("the message")));
            L.run("pcall(throws); assert(java.catched():getMessage() == 'the message')");

            L.push("message");
            assertThrows(LuaError.class,
                    () -> JavaLib.checkOrError(LuaJNatives.instances.get((int) L.getPointer()), -1));
        }
    }

    @Test
    public void loadPackages() {
        try (LuaJ L = new LuaJ()) {
            L.openLibrary("");
            L.openLibrary("package");
            L.openLibrary("io");
            L.openLibrary("coroutine");
            L.openLibrary("debug");
            L.openLibrary("io");
            L.openLibrary("math");
            L.openLibrary("os");
            L.openLibrary("string");
            L.openLibrary("table");
        }
    }

}
