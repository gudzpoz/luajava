package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class JuaJNITests {
    public static class JuaTest extends Jua implements Runnable {
        public JuaTest() {
            /* luaL_newstate */
        }

        /* Compacting tests into a single method for threaded tests */
        @Override
        public void run() {
            /* lua_gettable, lua_gettop, lua_pcall, lua_pop,
               are used everywhere
             */

            /* luaL_callmeta, lua_getmetatable, lua_setmetatable */
            // a: table, b: metatable
            assertEquals(0, luaL_dostring(L, "a = {};" +
                    "b = { __call = function(i) i['called'] = true end };" +
                    "c = function(i) i['meta'] = true; return 1 end;"));
            lua_getglobal(L, "a");
            assertEquals(0, luaL_callmeta(L, -1, "__call"));
            lua_getglobal(L, "b");
            // lua document states nothing about return values of lua_setmetatable
            assertEquals(1, lua_setmetatable(L, -2));
            lua_getglobal(L, "b");
            lua_getmetatable(L, -2);
            assertEquals(1, lua_equal(L, -1, -2));
            lua_pop(L, 2);
            // pushes the return value of __call, that is nil in this case
            assertEquals(1, luaL_callmeta(L, -1, "__call"));
            assertEquals(2, lua_gettop(L));
            assertEquals(1, lua_isnil(L, -1));
            // discard metatable
            assertEquals(1, lua_setmetatable(L, -2));
            assertEquals(0, luaL_callmeta(L, -1, "__call"));
            lua_settop(L, 0);

            /* luaL_dostring */
            assertEquals(0, luaL_dostring(L, ""));
            assertEquals(1, luaL_dostring(L, "{"));
            assertEquals(1, lua_gettop(L));
            assertEquals(Consts.LUA_TSTRING, lua_type(L, -1));
            lua_pop(L, 1);

            /* luaL_getmetafield */
            lua_getglobal(L, "a");
            assertEquals(0, luaL_getmetafield(L, -1, "__call"));
            assertEquals(1, lua_gettop(L));
            lua_getglobal(L, "b");
            assertEquals(1, lua_setmetatable(L, -2));
            assertEquals(1, luaL_getmetafield(L, -1, "__call"));
            assertEquals(2, lua_gettop(L));
            lua_pop(L, 2);

            /* luaL_getmetatable, luaL_newmetatable */
            assertEquals(1, luaL_newmetatable(L, "metatable4userdata"));
            lua_pushstring(L, "__call");
            lua_getglobal(L, "c");
            lua_settable(L, -3);
            lua_pop(L, 1);
            lua_newtable(L);
            luaL_getmetatable(L, "metatable4userdata");
            assertEquals(1, lua_setmetatable(L, -2));
            assertEquals(1, luaL_callmeta(L, -1, "__call"));
            assertEquals(1, lua_tointeger(L, -1));
            lua_pop(L, 2);

            /* luaL_loadbuffer */
            String cjk = "阿あㅏ";
            String s = "t = '" + cjk + "'";
            byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocateDirect(sBytes.length);
            buffer.put(sBytes);
            buffer.flip();
            assertTrue(buffer.isDirect());
            assertEquals(sBytes.length, buffer.limit());
            assertEquals(0,
                    luaL_loadbuffer(L, buffer, buffer.limit(),
                            "test.lua"));
            assertDoesNotThrow(() ->
                    assertEquals(0, lua_pcall(L, 0, Consts.LUA_MULTRET, 0))
            );
            lua_getglobal(L, "t");
            assertEquals(cjk, lua_tostring(L, -1));
            lua_pop(L, 1);

            /* luaL_loadstring */
            assertEquals(0, luaL_loadstring(L, "t = 'abc'"));
            assertDoesNotThrow(() ->
                    assertEquals(0, lua_pcall(L, 0, Consts.LUA_MULTRET, 0))
            );
            lua_getglobal(L, "t");
            assertEquals("abc", lua_tostring(L, -1));
            lua_pop(L, 1);

            /* luaL_openlibs, luaopen_* */
            lua_getglobal(L, "jit");
            assertEquals(1, lua_istable(L, -1));
            lua_pop(L, 1);
            checkPackage("package", l -> luaopen_package(L));
            checkPackage("table", l -> luaopen_table(L));
            checkPackage("io", l -> luaopen_io(L));
            checkPackage("os", l -> luaopen_os(L));
            checkPackage("string", l -> luaopen_string(L));
            checkPackage("math", l -> luaopen_math(L));
            checkPackage("debug", l -> luaopen_debug(L));
            checkPackage("bit", l -> luaopen_bit(L));

            /* luaL_ref, luaL_unref */
            lua_pushnil(L);
            int ref = luaL_ref(L, Consts.LUA_REGISTRYINDEX);
            assertEquals(Consts.LUA_REFNIL, ref);
            lua_pushnumber(L, 42);
            ref = luaL_ref(L, Consts.LUA_REGISTRYINDEX);
            lua_rawgeti(L, Consts.LUA_REGISTRYINDEX, ref);
            assertEquals(42, lua_tonumber(L, -1));
            lua_pop(L, 1);
            luaL_unref(L, Consts.LUA_REGISTRYINDEX, ref);
            lua_rawgeti(L, Consts.LUA_REGISTRYINDEX, ref);
            assertEquals(1, lua_isnil(L, -1));
            lua_pop(L, 1);

            /* lua_concat, lua_equals */
            lua_pushstring(L, "Hello");
            lua_pushstring(L, " ");
            lua_pushstring(L, "World");
            lua_concat(L, 3);
            assertEquals("Hello World", lua_tostring(L, -1));
            lua_pushstring(L, "Hello World");
            assertEquals(1, lua_equal(L, -1, -2));
            lua_pop(L, 2);

            /* lua_createtable */
            lua_createtable(L, 0, 0);
            assertEquals(1, lua_istable(L, -1));
            lua_pop(L, 1);

            /* lua_gc */
            //noinspection ResultOfMethodCallIgnored
            lua_gc(L, Consts.LUA_GCCOLLECT, 0);
            long gc = getGc();
            //noinspection ResultOfMethodCallIgnored
            lua_newuserdata(L, 10240);
            assertTrue(getGc() >= gc + 10240);
            gc = getGc();
            lua_pop(L, 1);
            //noinspection ResultOfMethodCallIgnored
            lua_gc(L, Consts.LUA_GCCOLLECT, 0);
            assertTrue(getGc() <= gc - 10240);

            /* lua_getfenv */
            lua_getglobal(L, "c");
            lua_getfenv(L, -1);
            assertEquals(1, lua_istable(L, -1));
            lua_pushstring(L, "b");
            lua_gettable(L, -2);
            assertEquals(1, lua_istable(L, -1));
            lua_getfield(L, -2, "b");
            assertEquals(1, lua_equal(L, -1, -2));
            lua_pop(L, 4);

            /* lua_insert */
            lua_pushnumber(L, 42);
            lua_pushnil(L);
            lua_insert(L, -2);
            assertEquals(42, lua_tointeger(L, -1));
            lua_pop(L, 2);

            /* lua_push*, lua_is* */
            lua_pushboolean(L, 1);
            assertEquals(1, lua_isboolean(L, -1));
            lua_getglobal(L, "print");
            assertEquals(1, lua_iscfunction(L, -1));
            assertEquals(1, lua_isfunction(L, -1));
            lua_pushlightuserdata(L, Long.MAX_VALUE);
            assertEquals(1, lua_islightuserdata(L, -1));
            lua_pushnil(L);
            assertEquals(1, lua_isnone(L, 12));
            assertEquals(0, lua_isnone(L, -1));
            assertEquals(1, lua_isnoneornil(L, 12));
            assertEquals(1, lua_isnoneornil(L, -1));
            lua_pushinteger(L, 42);
            assertEquals(1, lua_isnumber(L, -1));
            lua_pushstring(L, "");
            assertEquals(1, lua_isstring(L, -1));
            long ptr = lua_newuserdata(L, 1024);
            assertEquals(ptr, lua_topointer(L, -1));
            assertEquals(ptr, lua_touserdata(L, -1));
            assertEquals(1, lua_isuserdata(L, -1));
            lua_pop(L, 7);

            /* lua_lessthan */
            lua_pushnumber(L, Double.NEGATIVE_INFINITY);
            lua_pushnumber(L, Double.POSITIVE_INFINITY);
            assertEquals(1, lua_lessthan(L, -2, -1));
            lua_pop(L, 2);

            /* lua_next, lua_objlen */
            lua_getglobal(L, "b");
            assertEquals(0, lua_objlen(L, -1));
            lua_pushnil(L);
            assertNotEquals(0, lua_next(L, -2));
            assertEquals(1, lua_isfunction(L, -1));
            lua_pop(L, 1);
            assertEquals(0, lua_next(L, -2));
            lua_pop(L, 1);

            /* lua_pushlstring, lua_pushvalue */
            lua_pushlstring(L, "Hello World", 5);
            lua_pushvalue(L, -1);
            assertEquals("Hello", lua_tostring(L, -1));
            assertEquals(1, lua_rawequal(L, -1, -2));
            lua_pop(L, 2);

            /* lua_rawget, lua_rawset (not testing metamethods though) */
            lua_getglobal(L, "a");
            lua_pushstring(L, "called");
            lua_rawget(L, -2);
            assertEquals(1, lua_toboolean(L, -1));
            lua_pop(L, 1);
            lua_pushstring(L, "called");
            lua_pushboolean(L, 0);
            lua_rawset(L, -3);
            lua_pushboolean(L, 0);
            lua_rawseti(L, -2, 0);
            lua_rawgeti(L, -1, 0);
            assertEquals(0, lua_toboolean(L, -1));
            lua_remove(L, -2);
            assertEquals(1, lua_isboolean(L, -1));
            lua_pop(L, 1);

            /* lua_replace */
            lua_pushstring(L, "A");
            lua_pushstring(L, "B");
            lua_replace(L, -2);
            assertEquals("B", lua_tostring(L, -1));
            lua_pop(L, 1);

            /* lua_setfenv, lua_setfield */
            lua_pushnil(L);
            lua_newtable(L);
            lua_pushstring(L, "");
            lua_setfield(L, -2, "");
            assertEquals(0, lua_setfenv(L, -2));
            lua_pop(L, 1);

            /* Threads:
               lua_isthread, lua_newthread, lua_pushthread,
               lua_resume, lua_status, lua_tothread, lua_xmove
             */

            /* Unsafe methods are disabled */
            unsafeMethods();

            dumpStack();

            /* lua_close */
            lua_close(L);
        }

        @SuppressWarnings("deprecation")
        private void unsafeMethods() {
            assertThrows(UnsupportedOperationException.class,
                    () -> lua_call(L, 0, Consts.LUA_MULTRET));
            assertThrows(UnsupportedOperationException.class,
                    () -> lua_error(L));
            assertThrows(UnsupportedOperationException.class,
                    () -> lua_pushcclosure(L, 0, 0));
            assertThrows(UnsupportedOperationException.class,
                    () -> lua_pushcfunction(L, 0));
        }

        private void checkPackage(String global, Consumer<Long> opener) {
            lua_getglobal(L, global);
            assertEquals(1, lua_isnil(L, -1));
            opener.accept(L);
            lua_getglobal(L, global);
            assertEquals(1, lua_istable(L, -1));
            lua_pop(L, 2);
        }

        private long getGc() {
            return lua_gc(L, Consts.LUA_GCCOUNT, 0) * 1024L
                    + lua_gc(L, Consts.LUA_GCCOUNTB, 0);
        }

        public void dumpStack() {
            luaL_where(L, 0);
            int top = lua_gettop(L);
            for (int i = 1; i <= top; ++i) {
                String name = luaL_typename(L, i);
                // System.out.println(name + ": " + lua_tostring(L, i));
                assertEquals(name, lua_typename(L, lua_type(L, i)));
            }
            assertEquals(1, top);
            lua_pop(L, 1);
        }
    }

    @Test
    public void test() {
        new JuaTest().run();
    }
}
