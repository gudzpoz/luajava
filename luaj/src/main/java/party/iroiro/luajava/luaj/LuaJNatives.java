package party.iroiro.luajava.luaj;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.DumpState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import party.iroiro.luajava.LuaInstances;
import party.iroiro.luajava.LuaNative;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

import static party.iroiro.luajava.luaj.LuaJConsts.*;


public class LuaJNatives extends LuaNative {
    final static LuaJInstances instances = new LuaJInstances();

    @Override
    public void loadAsGlobal() {
        // Pointless
    }

    @Override
    protected int getRegistryIndex() {
        return LuaJConsts.LUA_REGISTRYINDEX;
    }

    @Override
    protected int lua_checkstack(long ptr, int extra) {
        // Pointless
        return 1;
    }

    @Override
    protected void lua_close(long ptr) {
        instances.remove((int) ptr);
    }

    @Override
    protected void lua_concat(long ptr, int n) {
        LuaJState L = instances.get((int) ptr);
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        if (n == 0) {
            L.push(LuaValue.valueOf(""));
            return;
        }
        if (n == 1) {
            return;
        }
        LuaValue value = L.toLuaValue(-n);
        for (int i = n - 1; i > 0; i++) {
            value = value.concat(L.toLuaValue(-i));
        }
        L.pop(n);
        L.push(value);
    }

    @Override
    protected void lua_createtable(long ptr, int narr, int nrec) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.tableOf(narr, nrec));
    }

    @Override
    protected int lua_error(long ptr) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void luaJ_getfield(long ptr, int index, String k) {
        LuaJState L = instances.get((int) ptr);
        L.push(L.toLuaValue(index).get(k));
    }

    @Override
    protected void luaJ_getglobal(long ptr, String name) {
        LuaJState L = instances.get((int) ptr);
        L.push(L.globals.get(name));
    }

    @Override
    protected int lua_getmetatable(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        LuaValue metatable = value.getmetatable();
        if (metatable.istable()) {
            L.push(metatable);
            return 1;
        }
        return 0;
    }

    @Override
    protected void luaJ_gettable(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        LuaValue key = L.toLuaValue(-1);
        L.push(value.get(key));
    }

    @Override
    protected int lua_gettop(long ptr) {
        LuaJState L = instances.get((int) ptr);
        return L.getTop();
    }

    @Override
    protected void lua_insert(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        L.insert(index, value);
    }

    @Override
    protected int lua_isboolean(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isboolean() ? 1 : 0;
    }

    @Override
    protected int lua_iscfunction(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isfunction() ? 1 : 0;
    }

    @Override
    protected int lua_isfunction(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isfunction() ? 1 : 0;
    }

    @Override
    protected int lua_islightuserdata(long ptr, int index) {
        return 0;
    }

    @Override
    protected int lua_isnil(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isnil() ? 1 : 0;
    }

    @Override
    protected int lua_isnone(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value == LuaValue.NONE ? 1 : 0;
    }

    @Override
    protected int lua_isnoneornil(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value == LuaValue.NONE || value.isnil() ? 1 : 0;
    }

    @Override
    protected int lua_isnumber(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isnumber() ? 1 : 0;
    }

    @Override
    protected int lua_isstring(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isstring() ? 1 : 0;
    }

    @Override
    protected int lua_istable(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.istable() ? 1 : 0;
    }

    @Override
    protected int lua_isthread(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isthread() ? 1 : 0;
    }

    @Override
    protected int lua_isuserdata(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.isuserdata() ? 1 : 0;
    }

    @Override
    protected void lua_newtable(long ptr) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.tableOf());
    }

    @Override
    protected long lua_newthread(long ptr) {
        LuaJState L = instances.get((int) ptr);
        LuaThread thread = new LuaThread(L.globals);
        LuaInstances.Token<LuaJState> handle = instances.add();
        handle.setter.accept(new LuaJState(handle.id, L.lid, L.globals, thread));
        return handle.id;
    }

    @Override
    protected int lua_next(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue table = L.toLuaValue(index);
        LuaValue key = L.toLuaValue(-1);
        L.pop(1);
        Varargs next = table.next(key);
        if (next.narg() != 2) {
            return 0;
        }
        L.push(next.arg(1));
        L.push(next.arg(2));
        return 1;
    }

    @Override
    protected int lua_pcall(long ptr, int nargs, int nresults, int errfunc) {
        LuaJState L = instances.get((int) ptr);
        LuaValue f = L.toLuaValue(-nargs - 1);
        LuaValue[] args = new LuaValue[nargs];
        for (int i = 0; i < nargs; i++) {
            args[i] = L.toLuaValue(-nargs + i);
        }
        Varargs results;
        LuaValue errorCallback = errfunc == 0 ? null : L.toLuaValue(errfunc);
        try {
            results = f.invoke(args);
        } catch (Exception e) {
            LuaValue message = LuaValue.valueOf(e.getMessage());
            if (errorCallback != null) {
                try {
                    message = errorCallback.call(message);
                } catch (Exception ex) {
                    L.pop(nargs + 1);
                    L.push(LuaValue.valueOf(ex.getMessage()));
                    return LUA_ERRERR;
                }
            }
            L.pop(nargs + 1);
            L.push(message);
            return LUA_ERRRUN;
        }
        L.pop(nargs + 1);
        if (nresults == LUA_MULTRET) {
            nresults = results.narg();
        }
        for (int i = 0; i < nresults; i++) {
            if (i < results.narg()) {
                L.push(results.arg(i + 1));
            } else {
                L.push(LuaValue.NIL);
            }
        }
        return 0;
    }

    @Override
    protected void luaJ_pcall(long ptr, int nargs, int nresults, int errfunc) {
        lua_pcall(ptr, nargs, nresults, errfunc);
    }

    @Override
    protected void lua_pop(long ptr, int n) {
        LuaJState L = instances.get((int) ptr);
        L.pop(n);
    }

    @Override
    protected void lua_pushboolean(long ptr, int b) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.valueOf(b != 0));
    }

    @Override
    protected void lua_pushinteger(long ptr, int n) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.valueOf(n));
    }

    @Override
    protected void lua_pushlightuserdata(long ptr, long p) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void lua_pushnil(long ptr) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.NIL);
    }

    @Override
    protected void lua_pushnumber(long ptr, double n) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.valueOf(n));
    }

    @Override
    protected void luaJ_pushstring(long ptr, String s) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.valueOf(s));
    }

    @Override
    protected int lua_pushthread(long ptr) {
        LuaJState L = instances.get((int) ptr);
        if (L.thread != null) {
            L.push(L.thread);
            return 0;
        } else {
            L.push(LuaValue.tableOf());
            return 1;
        }
    }

    @Override
    protected void lua_pushvalue(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        L.push(value);
    }

    @Override
    protected int lua_rawequal(long ptr, int index1, int index2) {
        LuaJState L = instances.get((int) ptr);
        LuaValue v1 = L.toLuaValue(index1);
        LuaValue v2 = L.toLuaValue(index1);
        return v1.raweq(v2) ? 1 : 0;
    }

    @Override
    protected void luaJ_rawget(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        LuaValue key = L.toLuaValue(-1);
        L.pop(1);
        L.push(value.rawget(key));
    }

    @Override
    protected void luaJ_rawgeti(long ptr, int index, int n) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        L.push(value.rawget(n));
    }

    @Override
    protected void lua_rawset(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue table = L.toLuaValue(index);
        LuaValue key = L.toLuaValue(-2);
        LuaValue value = L.toLuaValue(-1);
        L.pop(2);
        table.rawset(key, value);
    }

    @Override
    protected void lua_rawseti(long ptr, int index, int n) {
        LuaJState L = instances.get((int) ptr);
        LuaValue table = L.toLuaValue(index);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        table.rawset(n, value);
    }

    @Override
    protected void lua_remove(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        L.remove(index);
    }

    @Override
    protected void lua_replace(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        L.replace(index, value);
    }

    @Override
    protected void lua_setfield(long ptr, int index, String k) {
        LuaJState L = instances.get((int) ptr);
        LuaValue table = L.toLuaValue(index);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        table.set(k, value);
    }

    @Override
    protected void lua_setglobal(long ptr, String name) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        L.globals.set(name, value);
    }

    @Override
    protected void luaJ_setmetatable(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        LuaValue meta = L.toLuaValue(-1);
        L.pop(1);
        value.setmetatable(meta);
    }

    @Override
    protected void lua_settable(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue table = L.toLuaValue(index);
        LuaValue key = L.toLuaValue(-2);
        LuaValue value = L.toLuaValue(-1);
        L.pop(2);
        table.set(key, value);
    }

    @Override
    protected void lua_settop(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        L.setTop(index);
    }

    @Override
    protected int lua_status(long ptr) {
        LuaJState L = instances.get((int) ptr);
        if (L.thread == null) {
            return 0;
        }
        if (Objects.equals(L.thread.getStatus(), "suspended")) {
            return LUA_YIELD;
        }
        return 0;
    }

    @Override
    protected int lua_toboolean(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.toboolean() ? 1 : 0;
    }

    @Override
    protected int lua_tointeger(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return (int) value.tolong();
    }

    @Override
    protected double lua_tonumber(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.todouble();
    }

    @Override
    protected long lua_topointer(long ptr, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String lua_tostring(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.tojstring();
    }

    @Override
    protected long lua_tothread(long ptr, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected long lua_touserdata(long ptr, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int lua_type(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.type();
    }

    @Override
    protected String lua_typename(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.typename();
    }

    @Override
    protected void lua_xmove(long from, long to, int n) {
        LuaJState L = instances.get((int) from);
        LuaJState J = instances.get((int) to);
        for (int i = 0; i < n; i++) {
            J.push(L.toLuaValue(-n + i));
        }
        L.pop(n);
    }

    @Override
    protected int lua_yield(long ptr, int nresults) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int lua_gethookcount(long ptr) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int lua_gethookmask(long ptr) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String lua_getupvalue(long ptr, int funcindex, int n) {
        LuaJState L = instances.get((int) ptr);
        LuaClosure value = (LuaClosure) L.toLuaValue(funcindex);
        L.push(value.upValues[n].getValue());
        return "";
    }

    @Override
    protected String lua_setupvalue(long ptr, int funcindex, int n) {
        LuaJState L = instances.get((int) ptr);
        LuaClosure up = (LuaClosure) L.toLuaValue(funcindex);
        LuaValue value = L.toLuaValue(-1);
        L.pop(1);
        up.upValues[n].setValue(value);
        return "";
    }

    @Override
    protected int luaL_callmeta(long ptr, int obj, String e) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(obj);
        LuaValue metatable = value.getmetatable();
        LuaValue meta = metatable.get(e);
        if (!meta.isfunction()) {
            return 0;
        }
        L.push(meta.call(value));
        return 1;
    }

    @Override
    protected int luaL_dostring(long ptr, String str) {
        LuaJState L = instances.get((int) ptr);
        L.push(L.globals.load(str));
        return lua_pcall(ptr, 0, LUA_MULTRET, 0);
    }

    @Override
    protected int luaL_getmetafield(long ptr, int obj, String e) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(obj);
        LuaValue metatable = value.getmetatable();
        LuaValue meta = metatable.get(e);
        if (meta.isnil()) {
            return 0;
        }
        L.push(value);
        return 1;

    }

    @Override
    protected void luaJ_getmetatable(long ptr, String tname) {
        LuaJState L = instances.get((int) ptr);
        L.push(L.getRegistry(tname));
    }

    @Override
    protected String luaL_gsub(long ptr, String s, String p, String r) {
        LuaJState L = instances.get((int) ptr);
        String result = s.replaceAll(p, r);
        L.push(LuaValue.valueOf(result));
        return result;
    }

    @Override
    protected int luaL_loadstring(long ptr, String s) {
        LuaJState L = instances.get((int) ptr);
        L.push(L.globals.load(s));
        return 0;
    }

    @Override
    protected int luaL_newmetatable(long ptr, String tname) {
        LuaJState L = instances.get((int) ptr);
        if (L.getRegistry(tname).isnil()) {
            L.setRegistry(tname, LuaValue.tableOf());
            return 1;
        }
        L.push(L.getRegistry(tname));
        return 0;
    }

    @Override
    protected void luaJ_newmetatable(long ptr, String tname) {
        luaL_newmetatable(ptr, tname);
    }

    @Override
    protected long luaL_newstate(int lid) {
        LuaInstances.Token<LuaJState> handle = instances.add();
        Globals globals = new Globals();
        LuaC.install(globals);
        LoadState.install(globals);
        LuaJState state = new LuaJState(handle.id, lid, globals, null);
        handle.setter.accept(state);
        return handle.id;
    }

    @Override
    protected void luaL_openlibs(long ptr) {
        LuaJState L = instances.get((int) ptr);
        L.globals.load(new BaseLib());
        L.globals.load(new PackageLib());

        L.globals.load(new JseIoLib());
        L.globals.load(new MathLib());
        L.globals.load(new OsLib());
        L.globals.load(new StringLib());
        L.globals.load(new TableLib());
    }

    @Override
    protected int luaL_ref(long ptr, int t) {
        LuaJState L = instances.get((int) ptr);
        LuaTable value = (LuaTable) L.toLuaValue(t);
        LuaValue i = value.rawget(0);
        int next = i.toint() + 1;
        value.rawset(0, LuaValue.valueOf(next));
        value.rawset(next, L.toLuaValue(-1));
        return next;
    }

    @Override
    protected String luaL_typename(long ptr, int index) {
        return lua_typename(ptr, index);
    }

    @Override
    protected void luaL_unref(long ptr, int t, int ref) {
        LuaJState L = instances.get((int) ptr);
        LuaTable value = (LuaTable) L.toLuaValue(t);
        value.rawset(ref, LuaValue.NIL);
    }

    @Override
    protected void luaL_where(long ptr, int lvl) {
        LuaJState L = instances.get((int) ptr);
        L.push(LuaValue.valueOf(""));
    }

    @Override
    protected void luaJ_openlib(long ptr, String lib) {
        LuaJState L = instances.get((int) ptr);
        switch (lib) {
            case "":
                L.globals.load(new BaseLib());
                break;
            case "package":
                L.globals.load(new PackageLib());
                break;
            case "io":
                L.globals.load(new JseIoLib());
                break;
            case "math":
                L.globals.load(new MathLib());
                break;
            case "os":
                L.globals.load(new OsLib());
                break;
            case "string":
                L.globals.load(new StringLib());
                break;
            case "table":
                L.globals.load(new TableLib());
                break;
        }
    }

    @Override
    protected int luaJ_compare(long ptr, int index1, int index2, int op) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value1 = L.toLuaValue(index1);
        LuaValue value2 = L.toLuaValue(index2);
        if (value1.lt(value2).toboolean()) {
            return 1;
        }
        if (value1.gt(value2).toboolean()) {
            return -1;
        }
        return 0;
    }

    @Override
    protected int luaJ_len(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        return value.length();
    }

    @Override
    protected int luaJ_loadbuffer(long ptr, Buffer buffer, int size, String name) {
        LuaJState L = instances.get((int) ptr);
        ByteBuffer bytes = (ByteBuffer) buffer;
        L.push(
                L.globals.load(new InputStream() {
                    @Override
                    public int read() {
                        return bytes.hasRemaining() ? bytes.get() & 0xFF: -1;
                    }
                }, name, "bt", L.globals)
        );
        return 0;
    }

    @Override
    protected int luaJ_dobuffer(long ptr, Buffer buffer, int size, String name) {
        luaJ_loadbuffer(ptr, buffer, size, name);
        return lua_pcall(ptr, 0, LUA_MULTRET, 0);
    }

    @Override
    protected int luaJ_pcall(long ptr, int nargs, int nresults) {
        return lua_pcall(ptr, nargs, nresults, 0);
    }

    @Override
    protected int luaJ_resume(long ptr, int nargs) {
        LuaJState L = instances.get((int) ptr);
        LuaValue[] args = new LuaValue[nargs];
        for (int i = 0; i < nargs; i++) {
            args[i] = L.toLuaValue(-nargs + i);
        }
        Varargs results = L.thread.resume(LuaValue.varargsOf(args));
        for (int i = 1; i <= results.narg(); i++) {
            L.push(results.arg(i));
        }
        return 0;
    }

    @Override
    protected void luaJ_pushobject(long ptr, Object obj) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected void luaJ_pushclass(long ptr, Object clazz) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected void luaJ_pusharray(long ptr, Object array) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected void luaJ_pushfunction(long ptr, Object func) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected int luaJ_isobject(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object luaJ_toobject(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected long luaJ_newthread(long ptr, int lid) {
        return lua_newthread(ptr);
    }

    @Override
    protected int luaJ_initloader(long ptr) {
        LuaJState L = instances.get((int) ptr);
        // TODO: init java bindings
        return 0;
    }

    @Override
    protected int luaJ_invokespecial(long ptr, @SuppressWarnings("rawtypes") Class clazz, String method, String sig, Object obj, String params) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override
    protected void luaJ_removestateindex(long ptr) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

    @Override

    protected void luaJ_gc(long ptr) {
        Runtime.getRuntime().gc();
    }

    @Override
    protected Object luaJ_dumptobuffer(long ptr) {
        LuaJState L = instances.get((int) ptr);
        LuaValue func = L.toLuaValue(-1);
        if (!func.isclosure()) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            Prototype p = ((LuaClosure) func).p;
            DumpState.dump(p, output, true);
            byte[] byteArray = output.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocateDirect(output.size());
            buffer.put(byteArray);
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            lua_error(ptr);
            return null;
        }
    }

    @Override
    protected Object luaJ_tobuffer(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        LuaValue value = L.toLuaValue(index);
        if (!value.isstring()) {
            return null;
        }
        byte[] bytes = ((LuaString) value).m_bytes;
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    @Override
    protected Object luaJ_todirectbuffer(long ptr, int index) {
        LuaJState L = instances.get((int) ptr);
        throw new UnsupportedOperationException();
    }

}
