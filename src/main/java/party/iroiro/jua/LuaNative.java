package party.iroiro.jua;

/**
 * Generated from the common parts of <code>Lua5.[1..4]</code>
 */
@SuppressWarnings("unused")
public abstract class LuaNative {

    protected abstract double lua_tonumber(long ptr, int index);

    protected abstract int lua_error(long ptr);

    protected abstract int lua_gethookcount(long ptr);

    protected abstract int lua_gethookmask(long ptr);

    protected abstract int lua_getmetatable(long ptr, int index);

    protected abstract int lua_gettop(long ptr);

    protected abstract int lua_isboolean(long ptr, int index);

    protected abstract int lua_iscfunction(long ptr, int index);

    protected abstract int lua_isfunction(long ptr, int index);

    protected abstract int lua_islightuserdata(long ptr, int index);

    protected abstract int lua_isnil(long ptr, int index);

    protected abstract int lua_isnone(long ptr, int index);

    protected abstract int lua_isnoneornil(long ptr, int index);

    protected abstract int lua_isnumber(long ptr, int index);

    protected abstract int lua_isstring(long ptr, int index);

    protected abstract int lua_istable(long ptr, int index);

    protected abstract int lua_isthread(long ptr, int index);

    protected abstract int lua_isuserdata(long ptr, int index);

    protected abstract int luaL_callmeta(long ptr, int obj, String e);

    protected abstract int luaL_dostring(long ptr, String str);

    protected abstract int luaL_getmetafield(long ptr, int obj, String e);

    protected abstract int luaL_loadstring(long ptr, String s);

    protected abstract int luaL_newmetatable(long ptr, String tname);

    protected abstract int luaL_ref(long ptr, int t);

    protected abstract int lua_next(long ptr, int index);

    protected abstract int lua_pushthread(long ptr);

    protected abstract int lua_rawequal(long ptr, int index1, int index2);

    protected abstract int lua_status(long ptr);

    protected abstract int lua_toboolean(long ptr, int index);

    protected abstract int lua_tointeger(long ptr, int index);

    protected abstract int lua_type(long ptr, int index);

    protected abstract int lua_yield(long ptr, int nresults);

    protected abstract long lua_newthread(long ptr);

    protected abstract long lua_topointer(long ptr, int index);

    protected abstract long lua_tothread(long ptr, int index);

    protected abstract long lua_touserdata(long ptr, int index);

    protected abstract String lua_getupvalue(long ptr, int funcindex, int n);

    protected abstract String luaL_gsub(long ptr, String s, String p, String r);

    protected abstract String luaL_typename(long ptr, int index);

    protected abstract String lua_setupvalue(long ptr, int funcindex, int n);

    protected abstract String lua_tostring(long ptr, int index);

    protected abstract String lua_typename(long ptr, int tp);

    protected abstract void lua_close(long ptr);

    protected abstract void lua_concat(long ptr, int n);

    protected abstract void lua_createtable(long ptr, int narr, int nrec);

    protected abstract void lua_insert(long ptr, int index);

    protected abstract void luaL_openlibs(long ptr);

    protected abstract void luaL_unref(long ptr, int t, int ref);

    protected abstract void luaL_where(long ptr, int lvl);

    protected abstract void lua_newtable(long ptr);

    protected abstract void lua_pop(long ptr, int n);

    protected abstract void lua_pushboolean(long ptr, int b);

    protected abstract void lua_pushinteger(long ptr, int n);

    protected abstract void lua_pushlightuserdata(long ptr, long p);

    protected abstract void lua_pushnil(long ptr);

    protected abstract void lua_pushnumber(long ptr, double n);

    protected abstract void lua_pushvalue(long ptr, int index);

    protected abstract void lua_rawset(long ptr, int index);

    protected abstract void lua_remove(long ptr, int index);

    protected abstract void lua_replace(long ptr, int index);

    protected abstract void lua_setfield(long ptr, int index, String k);

    protected abstract void lua_setglobal(long ptr, String name);

    protected abstract void lua_settable(long ptr, int index);

    protected abstract void lua_settop(long ptr, int index);

    protected abstract void lua_xmove(long from, long to, int n);

    protected abstract void luaJ_openlib(long ptr, String name);
}
