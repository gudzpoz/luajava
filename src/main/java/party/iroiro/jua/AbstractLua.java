package party.iroiro.jua;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.*;

public abstract class AbstractLua implements Lua {
    protected static LuaInstances instances = new LuaInstances();

    public Lua getInstance(int lid) {
        return instances.get(lid);
    }

    protected final LuaNative C;
    protected final long L;
    protected final int id;
    protected final Lua mainThread;
    protected final List<Lua> subThreads;

    protected AbstractLua(LuaNative luaNative) {
        this.C = luaNative;
        id = instances.add(this);
        L = luaNative.luaL_newstate(id);
        mainThread = this;
        subThreads = new LinkedList<>();
    }

    protected AbstractLua(LuaNative luaNative, long L, int id, @NotNull Lua mainThread) {
        this.C = luaNative;
        this.L = L;
        this.mainThread = mainThread;
        this.id = id;
        subThreads = null;
    }

    @Override
    public void push(@Nullable Object object, Conversion degree) {
        if (object == null) {
            C.lua_pushnil(L);
        } else if (degree == Lua.Conversion.NONE) {
            pushJavaObjectOrArray(object);
        } else {
            if (object instanceof Boolean) {
                push((boolean) object);
            } else if (object instanceof String) {
                push((String) object);
            } else if (object instanceof Integer || object instanceof Byte || object instanceof Short) {
                push(((Number) object).intValue());
            } else if (object instanceof Long) {
                push((long) object);
            } else if (object instanceof Number) {
                push((Number) object);
            } else if (degree == Lua.Conversion.SEMI) {
                pushJavaObjectOrArray(object);
            } else /* (degree == Conversion.FULL) */ {
                if (object instanceof Map) {
                    push((Map<?, ?>) object);
                } else if (object instanceof Collection) {
                    push((Collection<?>) object);
                } else {
                    pushJavaObjectOrArray(object);
                }
            }
        }
    }

    protected void pushJavaObjectOrArray(Object object) {
        if (object.getClass().isArray()) {
            pushJavaArray(object);
        } else {
            pushJavaObject(object);
        }
    }

    protected <T> T notNull(T t) throws IllegalArgumentException {
        if (t == null) {
            throw new IllegalArgumentException("Invalid argument");
        } else {
            return t;
        }
    }

    @Override
    public void pushNil() {
        C.lua_pushnil(L);
    }

    @Override
    public void push(boolean bool) {
        C.lua_pushboolean(L, bool ? 1 : 0);
    }

    @Override
    public void push(@NotNull Number number) {
        C.lua_pushnumber(L, number.doubleValue());
    }

    @Override
    public void push(int integer) {
        C.lua_pushinteger(L, integer);
    }

    @Override
    public void push(@NotNull String string) {
        C.luaJ_pushstring(L, string);
    }

    @Override
    public void push(@NotNull Map<?, ?> map) {
        C.lua_createtable(L, 0, map.size());
        map.forEach((k, v) -> {
            push(k, Conversion.FULL);
            push(v, Conversion.FULL);
            C.lua_rawset(L, -3);
        });
    }

    @Override
    public void push(@NotNull Collection<?> collection) {
        C.lua_createtable(L, collection.size(), 0);
        int i = 1;
        for (Object o : collection) {
            push(o, Conversion.FULL);
            C.luaJ_rawgeti(L, -2, i);
            i++;
        }
    }

    @Override
    public void push(@NotNull JFunction function) {
        pushJavaObject(function);
    }

    @Override
    public void pushJavaObject(@NotNull Object object) throws IllegalArgumentException {
        if (object.getClass().isArray()) {
            throw new IllegalArgumentException("Expecting non-array argument");
        } else {
            C.luaJ_pushobject(L, object);
        }
    }

    @Override
    public void pushJavaArray(@NotNull Object array) throws IllegalArgumentException {
        if (array.getClass().isArray()) {
            C.luaJ_pusharray(L, array);
        } else {
            throw new IllegalArgumentException("Expecting non-array argument");
        }
    }

    @Override
    public void pushJavaClass(@NotNull Class<?> clazz) {
        C.luaJ_pushclass(L, clazz);
    }

    @Override
    public double toNumber(int index) throws IllegalArgumentException {
        return C.lua_tonumber(L, index);
    }

    @Override
    public boolean toBoolean(int index) throws IllegalArgumentException {
        return C.lua_toboolean(L, index) != 0;
    }

    @Override
    public @NotNull Object toObject(int index) throws IllegalArgumentException {
        return notNull(C.luaJ_toobject(L, index));
    }

    @Override
    public @NotNull String toString(int index) throws IllegalArgumentException {
        return notNull(C.lua_tostring(L, index));
    }

    @Override
    public @NotNull Object toJavaObject(int index) throws IllegalArgumentException {
        return notNull(C.luaJ_toobject(L, index));
    }

    @Override
    public @NotNull Map<?, ?> toMap(int index) throws IllegalArgumentException {
        try {
            Object obj = toJavaObject(index);
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj);
            }
        } catch (IllegalArgumentException ignored) {
        }
        if (C.lua_istable(L, index) == 1) {
            C.lua_pushnil(L);
            Map<Object, Object> map = new HashMap<>();
            while (C.lua_next(L, -2) != 0) {
                try {
                    map.put(toObject(-2), toObject(-1));
                } catch (IllegalArgumentException ignored) {
                }
                C.lua_pop(L, 1);
            }
            return map;
        }
        throw new IllegalArgumentException("Not a Java map or luatable");
    }

    @Override
    public @NotNull List<?> toList(int index) throws IllegalArgumentException {
        try {
            Object obj = toJavaObject(index);
            if (obj instanceof List) {
                return ((List<?>) obj);
            }
        } catch (IllegalArgumentException ignored) {
        }
        int top = C.lua_gettop(L);
        if (C.lua_istable(L, index) == 1) {
            C.luaJ_getglobal(L, "unpack");
            C.lua_pushvalue(L, -2);
            if (C.luaJ_pcall(L, 1, Consts.LUA_MULTRET) == 0) {
                int len = C.lua_gettop(L);
                ArrayList<Object> list = new ArrayList<>();
                list.ensureCapacity(len - top + 1);
                for (int i = top + 1; i <= len; ++i) {
                    try {
                        list.add(toObject(i));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                C.lua_settop(L, top);
                return list;
            } else {
                C.lua_settop(L, top);
                throw new IllegalArgumentException("unpack exception");
            }
        }
        throw new IllegalArgumentException("Not a Java list or luatable");
    }

    @Override
    public boolean isBoolean(int index) {
        return C.lua_isboolean(L, index) != 0;
    }

    @Override
    public boolean isFunction(int index) {
        return C.lua_isfunction(L, index) != 0;
    }

    @Override
    public boolean isJavaObject(int index) {
        return C.luaJ_isobject(L, index) != 0;
    }

    @Override
    public boolean isNil(int index) {
        return C.lua_isnil(L, index) != 0;
    }

    @Override
    public boolean isNone(int index) {
        return C.lua_isnone(L, index) != 0;
    }

    @Override
    public boolean isNoneOrNil(int index) {
        return C.lua_isnoneornil(L, index) != 0;
    }

    @Override
    public boolean isNumber(int index) {
        return C.lua_isnumber(L, index) != 0;
    }

    @Override
    public boolean isString(int index) {
        return C.lua_isstring(L, index) != 0;
    }

    @Override
    public boolean isTable(int index) {
        return C.lua_istable(L, index) != 0;
    }

    @Override
    public boolean isThread(int index) {
        return C.lua_isthread(L, index) != 0;
    }

    @Override
    public boolean isUserdata(int index) {
        return C.lua_isuserdata(L, index) != 0;
    }

    @Override
    public @Nullable LuaType type(int index) {
        return LuaType.valueOf(C, C.lua_type(L, index));
    }

    @Override
    public boolean equal(int i1, int i2) {
        return C.luaJ_compare(L, i1, i2, 0) != 0;
    }

    @Override
    public int length(int index) {
        return C.luaJ_len(L, index);
    }

    @Override
    public boolean lessThan(int i1, int i2) {
        return C.luaJ_compare(L, i1, i2, -1) != 0;
    }

    @Override
    public boolean rawEqual(int i1, int i2) {
        return C.lua_rawequal(L, i1, i2) != 0;
    }

    @Override
    public int getTop() {
        return C.lua_gettop(L);
    }

    @Override
    public void setTop(int index) {
        C.lua_settop(L, index);
    }

    @Override
    public void insert(int index) {
        C.lua_insert(L, index);
    }

    @Override
    public void pop(int n) {
        C.lua_pop(L, n);
    }

    @Override
    public void pushValue(int index) {
        C.lua_pushvalue(L, index);
    }

    @Override
    public void remove(int index) {
        C.lua_remove(L, index);
    }

    @Override
    public void replace(int index) {
        C.lua_replace(L, index);
    }

    @Override
    public void xMove(Lua other, int n) throws IllegalArgumentException {
        C.lua_xmove(L, other.getPointer(), n);
    }

    @Override
    public LuaError load(String script) {
        C.luaL_loadstring(L, script);
        // TODO: Conversion
        return LuaError.NONE;
    }

    @Override
    public LuaError load(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            C.luaJ_loadbuffer(L, buffer, buffer.limit(), name);
            return LuaError.NONE;
        } else {
            return LuaError.MEMORY;
        }
    }

    @Override
    public LuaError run(String script) {
        C.luaL_dostring(L, script);
        return LuaError.NONE;
    }

    @Override
    public LuaError run(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            C.luaJ_dobuffer(L, buffer, buffer.limit(), name);
            return LuaError.NONE;
        } else {
            return LuaError.MEMORY;
        }
    }

    @Override
    public int pCall(int nArgs, int nResults) {
        return C.luaJ_pcall(L, nArgs, nResults);
    }

    public Lua newThread() {
        LuaInstances.Token token = instances.add();
        long K = C.luaJ_newthread(L, token.id);
        return newThread(K, token.id, this.mainThread);
    }

    protected abstract Lua newThread(long L, int id, Lua mainThread);

    @Override
    public LuaError resume(int nArgs) {
        C.luaJ_resume(L, nArgs);
        return LuaError.NONE;
    }

    @Override
    public LuaError status() {
        C.lua_status(L);
        return LuaError.NONE;
    }

    @Override
    public void yield(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void createTable(int nArr, int nRec) {
        C.lua_createtable(L, nArr, nArr);
    }

    @Override
    public void getField(int index, String key) {
        C.luaJ_getfield(L, index, key);
    }

    @Override
    public void setField(int index, String key) {
        C.lua_setfield(L, index, key);
    }

    @Override
    public void getTable(int index) {
        C.luaJ_gettable(L, index);
    }

    @Override
    public void setTable(int index) {
        C.lua_settable(L, index);
    }

    @Override
    public void next(int n) {
        C.lua_next(L, n);
    }

    @Override
    public void rawGet(int index) {
        C.luaJ_rawget(L, index);
    }

    @Override
    public void rawGetI(int index, int n) {
        C.luaJ_rawgeti(L, index, n);
    }

    @Override
    public void rawSet(int index) {
        C.lua_rawset(L, index);
    }

    @Override
    public void rawSetI(int index, int n) {
        C.luaJ_rawgeti(L, index, n);
    }

    @Override
    public int ref(int index) {
        return C.luaL_ref(L, index);
    }

    @Override
    public void unRef(int index, int ref) {
        C.luaL_unref(L, index, ref);
    }

    @Override
    public void getGlobal(String name) {
        C.luaJ_getglobal(L, name);
    }

    @Override
    public void setGlobal(String name) {
        C.lua_setglobal(L, name);
    }

    @Override
    public int getMetatable(int index) {
        return C.lua_getmetatable(L, index);
    }

    @Override
    public void setMetatable(int index) {
        C.luaJ_setmetatable(L, index);
    }

    @Override
    public int getMetaField(int index, String field) {
        return C.luaL_getmetafield(L, index, field);
    }

    @Override
    public void getRegisteredMetatable(String typeName) {
        C.luaJ_getmetatable(L, typeName);
    }

    @Override
    public int newRegisteredMetatable(String typeName) {
        return C.luaL_newmetatable(L, typeName);
    }

    @Override
    public void openLibraries() {
        C.luaL_openlibs(L);
    }

    @Override
    public void openLibrary(String name) {
        C.luaJ_openlib(L, name);
    }

    @Override
    public void concat(int n) {
        C.lua_concat(L, n);
    }

    @Override
    public void error(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public void createProxy(Class<?>[] interfaces) {

    }

    @Override
    public LuaNative getLuaNative() {
        return C;
    }

    @Override
    public long getPointer() {
        return L;
    }

    @Override
    public void close() {
        instances.remove(id);
        C.lua_close(L);
    }
}
