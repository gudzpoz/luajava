package party.iroiro.luajava;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.nio.Buffer;
import java.util.*;

/**
 * An implementation that relys on {@link LuaNative} for most of the features independent of Lua versions
 */
public abstract class AbstractLua implements Lua {
    protected static LuaInstances<AbstractLua> instances = new LuaInstances<>();

    public static AbstractLua getInstance(int lid) {
        return instances.get(lid);
    }

    protected final LuaNative C;
    protected final long L;
    protected final int id;
    protected final AbstractLua mainThread;
    protected final List<Lua> subThreads;

    protected AbstractLua(LuaNative luaNative) {
        this.C = luaNative;
        id = instances.add(this);
        L = luaNative.luaL_newstate(id);
        mainThread = this;
        subThreads = new LinkedList<>();
    }

    protected AbstractLua(LuaNative luaNative, long L, int id, @NotNull AbstractLua mainThread) {
        this.C = luaNative;
        this.L = L;
        this.mainThread = mainThread;
        this.id = id;
        subThreads = null;
    }

    public static int adopt(int mainId, long ptr) {
        AbstractLua lua = getInstance(mainId);
        LuaInstances.Token<AbstractLua> token = instances.add();
        AbstractLua child = lua.newThread(ptr, token.id, lua);
        lua.addSubThread(child);
        token.setter.accept(child);
        return token.id;
    }

    @Override
    public void checkStack(int extra) throws RuntimeException {
        if (C.lua_checkstack(L, extra) == 0) {
            throw new RuntimeException("No more stack space available");
        }
    }

    @Override
    public void push(@Nullable Object object, Conversion degree) {
        checkStack(1);
        if (object == null) {
            pushNil();
        } else if (degree == Lua.Conversion.NONE) {
            pushJavaObjectOrArray(object);
        } else {
            if (object instanceof Boolean) {
                push((boolean) object);
            } else if (object instanceof String) {
                push((String) object);
            } else if (object instanceof Integer || object instanceof Byte || object instanceof Short) {
                push(((Number) object).intValue());
            } else if (object instanceof Character) {
                push(((int) (Character) object));
            } else if (object instanceof Long) {
                push((long) object);
            } else if (object instanceof Float || object instanceof Double) {
                push((Number) object);
            } else if (object instanceof JFunction) {
                push(((JFunction) object));
            } else if (degree == Lua.Conversion.SEMI) {
                pushJavaObjectOrArray(object);
            } else /* (degree == Conversion.FULL) */ {
                if (object instanceof Class) {
                    pushJavaClass(((Class<?>) object));
                } else if (object instanceof Map) {
                    push((Map<?, ?>) object);
                } else if (object instanceof Collection) {
                    push((Collection<?>) object);
                } else if (object.getClass().isArray()) {
                    pushArray(object);
                } else {
                    pushJavaObject(object);
                }
            }
        }
    }

    protected void pushJavaObjectOrArray(Object object) {
        checkStack(1);
        if (object.getClass().isArray()) {
            pushJavaArray(object);
        } else {
            pushJavaObject(object);
        }
    }

    @Override
    public void pushNil() {
        checkStack(1);
        C.lua_pushnil(L);
    }

    @Override
    public void push(boolean bool) {
        checkStack(1);
        C.lua_pushboolean(L, bool ? 1 : 0);
    }

    @Override
    public void push(@NotNull Number number) {
        checkStack(1);
        C.lua_pushnumber(L, number.doubleValue());
    }

    @Override
    public void push(int integer) {
        checkStack(1);
        C.lua_pushinteger(L, integer);
    }

    @Override
    public void push(@NotNull String string) {
        checkStack(1);
        C.luaJ_pushstring(L, string);
    }

    @Override
    public void push(@NotNull Map<?, ?> map) {
        checkStack(3);
        C.lua_createtable(L, 0, map.size());
        map.forEach((k, v) -> {
            push(k, Conversion.FULL);
            push(v, Conversion.FULL);
            C.lua_rawset(L, -3);
        });
    }

    @Override
    public void push(@NotNull Collection<?> collection) {
        checkStack(2);
        C.lua_createtable(L, collection.size(), 0);
        int i = 1;
        for (Object o : collection) {
            push(o, Conversion.FULL);
            C.lua_rawseti(L, -2, i);
            i++;
        }
    }

    @Override
    public void pushArray(@NotNull Object array) throws IllegalArgumentException {
        checkStack(2);
        if (array.getClass().isArray()) {
            int len = Array.getLength(array);
            C.lua_createtable(L, len, 0);
            for (int i = 0; i != len; ++i) {
                push(Array.get(array, i), Conversion.FULL);
                C.lua_rawseti(L, -2, i + 1);
            }
        } else {
            throw new IllegalArgumentException("Not a array");
        }
    }

    @Override
    public void push(@NotNull JFunction function) {
        checkStack(1);
        C.luaJ_pushfunction(L, function);
    }

    @Override
    public void pushJavaObject(@NotNull Object object) throws IllegalArgumentException {
        if (object.getClass().isArray()) {
            throw new IllegalArgumentException("Expecting non-array argument");
        } else {
            checkStack(1);
            C.luaJ_pushobject(L, object);
        }
    }

    @Override
    public void pushJavaArray(@NotNull Object array) throws IllegalArgumentException {
        if (array.getClass().isArray()) {
            checkStack(1);
            C.luaJ_pusharray(L, array);
        } else {
            throw new IllegalArgumentException("Expecting non-array argument");
        }
    }

    @Override
    public void pushJavaClass(@NotNull Class<?> clazz) {
        checkStack(1);
        C.luaJ_pushclass(L, clazz);
    }

    @Override
    public double toNumber(int index) {
        return C.lua_tonumber(L, index);
    }

    @Override
    public boolean toBoolean(int index) {
        return C.lua_toboolean(L, index) != 0;
    }

    @Override
    public @Nullable Object toObject(int index) {
        LuaType type = type(index);
        if (type == null) {
            return null;
        }
        switch (type) {
            case NIL:
                return null;
            case BOOLEAN:
                return toBoolean(index);
            case NUMBER:
                return toNumber(index);
            case STRING:
                return toString(index);
            case TABLE:
                return toMap(index);
            case USERDATA:
                return toJavaObject(index);
        }
        return null;
    }

    @Override
    public @Nullable Object toObject(int index, Class<?> type) {
        Object converted = toObject(index);
        if (converted == null) {
            return null;
        } else if (type.isAssignableFrom(converted.getClass())) {
            return converted;
        } else if (Number.class.isAssignableFrom(converted.getClass())) {
            Number number = ((Number) converted);
            if (type == byte.class || type == Byte.class) {
                return number.byteValue();
            }
            if (type == short.class || type == Short.class) {
                return number.shortValue();
            }
            if (type == int.class || type == Integer.class) {
                return number.intValue();
            }
            if (type == long.class || type == Long.class) {
                return number.longValue();
            }
            if (type == float.class || type == Float.class) {
                return number.floatValue();
            }
            if (type == double.class || type == Double.class) {
                return number.doubleValue();
            }
        }
        return null;
    }

    @Override
    public @Nullable String toString(int index) {
        return C.lua_tostring(L, index);
    }

    @Override
    public @Nullable Object toJavaObject(int index) {
        return C.luaJ_toobject(L, index);
    }

    @Override
    public @Nullable Map<?, ?> toMap(int index) {
        Object obj = toJavaObject(index);
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj);
        }
        checkStack(2);
        if (C.lua_istable(L, index) == 1) {
            C.lua_pushnil(L);
            Map<Object, Object> map = new HashMap<>();
            while (C.lua_next(L, -2) != 0) {
                Object k = toObject(-2);
                Object v = toObject(-1);
                if (k != null && v != null) {
                    map.put(k, v);
                }
                pop(1);
            }
            return map;
        }
        return null;
    }

    @Override
    public @Nullable List<?> toList(int index) {
        Object obj = toJavaObject(index);
        if (obj instanceof List) {
            return ((List<?>) obj);
        }
        checkStack(1);
        if (C.lua_istable(L, index) == 1) {
            int length = rawLength(index);
            ArrayList<Object> list = new ArrayList<>();
            list.ensureCapacity(length);
            for (int i = 1; i <= length; i++) {
                C.luaJ_rawgeti(L, index, i);
                list.add(toObject(-1));
                pop(1);
            }
            return list;
        }
        return null;
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
        return convertType(C.lua_type(L, index));
    }

    @Override
    public boolean equal(int i1, int i2) {
        return C.luaJ_compare(L, i1, i2, 0) != 0;
    }

    @Override
    public int rawLength(int index) {
        /* luaJ_len might push the length on stack then pop it. */
        checkStack(1);
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
        checkStack(1);
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
        if (other instanceof AbstractLua && ((AbstractLua) other).mainThread == mainThread) {
            other.checkStack(n);
            C.lua_xmove(L, other.getPointer(), n);
        } else {
            throw new IllegalArgumentException("Not sharing same global state");
        }
    }

    @Override
    public LuaError load(String script) {
        checkStack(1);
        return convertError(C.luaL_loadstring(L, script));
    }

    @Override
    public LuaError load(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            checkStack(1);
            return convertError(C.luaJ_loadbuffer(L, buffer, buffer.limit(), name));
        } else {
            return LuaError.MEMORY;
        }
    }

    @Override
    public LuaError run(String script) {
        checkStack(1);
        return C.luaL_dostring(L, script) == 0 ? LuaError.OK : LuaError.RUNTIME;
    }

    @Override
    public LuaError run(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            checkStack(1);
            return C.luaJ_dobuffer(L, buffer, buffer.limit(), name) == 0 ? LuaError.OK : LuaError.RUNTIME;
        } else {
            return LuaError.MEMORY;
        }
    }

    @Override
    public LuaError pCall(int nArgs, int nResults) {
        return convertError(C.luaJ_pcall(L, nArgs, nResults));
    }

    @Override
    public AbstractLua newThread() {
        checkStack(1);
        LuaInstances.Token<AbstractLua> token = instances.add();
        long K = C.luaJ_newthread(L, token.id);
        AbstractLua lua = newThread(K, token.id, this.mainThread);
        mainThread.addSubThread(lua);
        token.setter.accept(lua);
        return lua;
    }

    public synchronized void addSubThread(Lua lua) {
        subThreads.add(lua);
    }

    protected abstract AbstractLua newThread(long L, int id, AbstractLua mainThread);

    @Override
    public LuaError resume(int nArgs) {
        return convertError(C.luaJ_resume(L, nArgs));
    }

    @Override
    public LuaError status() {
        return convertError(C.lua_status(L));
    }

    @Override
    public void yield(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void createTable(int nArr, int nRec) {
        checkStack(1);
        C.lua_createtable(L, nArr, nArr);
    }

    @Override
    public void getField(int index, String key) {
        checkStack(1);
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
    public int next(int n) {
        checkStack(1);
        return C.lua_next(L, n);
    }

    @Override
    public void rawGet(int index) {
        C.luaJ_rawget(L, index);
    }

    @Override
    public void rawGetI(int index, int n) {
        checkStack(1);
        C.luaJ_rawgeti(L, index, n);
    }

    @Override
    public void rawSet(int index) {
        C.lua_rawset(L, index);
    }

    @Override
    public void rawSetI(int index, int n) {
        C.lua_rawseti(L, index, n);
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
        checkStack(1);
        C.luaJ_getglobal(L, name);
    }

    @Override
    public void setGlobal(String name) {
        C.lua_setglobal(L, name);
    }

    @Override
    public int getMetatable(int index) {
        checkStack(1);
        return C.lua_getmetatable(L, index);
    }

    @Override
    public void setMetatable(int index) {
        C.luaJ_setmetatable(L, index);
    }

    @Override
    public int getMetaField(int index, String field) {
        checkStack(1);
        return C.luaL_getmetafield(L, index, field);
    }

    @Override
    public void getRegisteredMetatable(String typeName) {
        checkStack(1);
        C.luaJ_getmetatable(L, typeName);
    }

    @Override
    public int newRegisteredMetatable(String typeName) {
        checkStack(1);
        return C.luaL_newmetatable(L, typeName);
    }

    @Override
    public void openLibraries() {
        checkStack(1);
        C.luaL_openlibs(L);
    }

    @Override
    public void openLibrary(String name) {
        checkStack(1);
        C.luaJ_openlib(L, name);
    }

    @Override
    public void concat(int n) {
        if (n == 0) {
            checkStack(1);
        }
        C.lua_concat(L, n);
    }

    @Override
    public void error(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public Object createProxy(Class<?>[] interfaces, Conversion degree) {
        if (isTable(-1)) {
            return Proxy.newProxyInstance(
                    Jua.class.getClassLoader(),
                    interfaces,
                    new LuaProxy(ref(), this, degree)
            );
        } else {
            pop(1);
            return null;
        }
    }

    @Override
    public void register(String name, JFunction function) {
        push(function);
        setGlobal(name);
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
    public int getId() {
        return id;
    }

    @Override
    public void close() {
        synchronized (mainThread) {
            if (mainThread == this) {
                for (Lua lua : subThreads) {
                    instances.remove(lua.getId());
                }
                subThreads.clear();
                instances.remove(id);
                C.lua_close(L);
            }
        }
    }

    @Override
    public int ref() {
        return ref(C.getRegistryIndex());
    }

    @Override
    public void refGet(int ref) {
        rawGetI(C.getRegistryIndex(), ref);
    }

    @Override
    public void unref(int ref) {
        unRef(C.getRegistryIndex(), ref);
    }

    public abstract LuaError convertError(int code);

    public abstract LuaType convertType(int code);
}
