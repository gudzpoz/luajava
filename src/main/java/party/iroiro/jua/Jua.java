package party.iroiro.jua;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.nio.Buffer;
import java.util.*;

import static party.iroiro.jua.JuaJitNatives.*;

/**
 * Wrapper of a <code>lua_State *</code> (LuaJIT)
 *
 * <p>The LuaJIT library and the JNI binding is automatically
 * loaded with {@link SharedLibraryLoader} and initialized.</p>
 *
 * <p><code>protected</code> functions that have identical names to lua ones
 * are mostly simple wrappers of the corresponding C API.</p>
 *
 * <p>Currently there are not many <code>public</code> methods.
 * If you want the plain lua C API, probably subclassing is the best.</p>
 *
 * @see <a href="https://www.lua.org/manual/5.1/manual.html#3">Lua 5.1 Reference Manual</a>
 */
public class Jua implements AutoCloseable {
    /**
     * The pointer, that is, the internal <code>lua_State *</code>
     */
    protected final long L;
    /**
     * Index to this Jua object
     *
     * <p>Used with {@link #get(int)} and stored also in the lua part,
     * to be used in the JNI part to identity the current state with
     * Java part</p>
     */
    protected final int stateId;
    /**
     * The main (lua) thread
     */
    protected final Jua mainThread;
    /**
     * Sub (lua) threads
     */
    protected final List<Jua> subThreads;

    private static final JuaInstances luaInstances = new JuaInstances();

    /**
     * Gets a {@link Jua} object by its index
     */
    public static Jua get(int id) {
        return luaInstances.get(id);
    }

    /**
     * Creates a new Jua object
     * <p>
     * See {@link JuaJitNatives#luaL_newstate} for details
     *
     * @throws UnsatisfiedLinkError when {@link SharedLibraryLoader} cannot load the native binaries
     */
    public Jua() throws UnsatisfiedLinkError {
        if (NATIVE_AVAILABLE) {
            stateId = luaInstances.add(this);
            this.L = luaL_newstate(stateId);
            subThreads = new LinkedList<>();
            mainThread = this;
        } else {
            throw new UnsatisfiedLinkError("Unable to bind to native binaries");
        }
    }

    protected synchronized void addSubThreads(Jua sub) {
        if (mainThread == this) {
            subThreads.add(sub);
        }
    }

    /**
     * Wraps a Jua object around a new lua thread
     */
    protected Jua(long L, Jua main) {
        stateId = luaInstances.add(this);
        this.L = L;
        subThreads = null;
        mainThread = main;
        mainThread.addSubThreads(this);
    }

    /**
     * Disposes with {@link JuaJitNatives#lua_close(long)}, only if the current state is the main state
     *
     * <p>Disposes the <code>lua_State *</code> and thus deleting relevant JNI object references.</p>
     *
     * <p>It does nothing if the current state is a sub thread. If so, you should
     * remove all references (in lua) to the current state and let lua handle
     * the garbage collection.</p>
     */
    public synchronized void dispose() {
        if (mainThread == this) {
            for (Jua thread : subThreads) {
                luaInstances.remove(thread.stateId);
            }
            subThreads.clear();
            luaInstances.remove(stateId);
            lua_close(L);
        }
    }

    /**
     * See {@link JuaJitNatives#luaL_dostring}
     */
    public int run(String s) {
        return luaL_dostring(L, s);
    }

    /**
     * Pushes element onto the stack
     */
    public void push(String string) {
        lua_pushstring(L, string);
    }

    /**
     * Pushes element onto the stack
     */
    public void push(int i) {
        lua_pushinteger(L, i);
    }

    /**
     * Pushes element onto the stack
     */
    public void push(long i) {
        lua_pushinteger(L, i);
    }

    /**
     * Pushes element onto the stack
     */
    public void push(Number n) {
        lua_pushnumber(L, n.doubleValue());
    }

    /**
     * Pushes element onto the stack
     */
    public void push(boolean b) {
        lua_pushboolean(L, b ? 1 : 0);
    }

    /**
     * Pushes onto the stack a luatable with key-value pairs from the map
     *
     * <p>Note that recursive reference is not supported and will overflow the stack.</p>
     */
    public void push(Map<?, ?> map) {
        lua_createtable(L, 0, map.size());
        map.forEach((k, v) -> {
            push(k);
            push(v);
            lua_rawset(L, -3);
        });
    }

    /**
     * Pushes onto the stack a luatable with key-value pairs from the array
     *
     * <p>The keys of the luatable starts from 1.</p>
     *
     * @param array   any array
     * @param convert {@code true} to convert the array to lua table
     */
    public void push(Object array, boolean convert) {
        if (convert) {
            int len = Array.getLength(array);
            lua_createtable(L, len, 0);
            for (int i = 0; i != len; ++i) {
                push(Array.get(array, i));
                lua_rawseti(L, -2, i + 1);
            }
        } else {
            jniPushJavaArray(L, array);
        }
    }

    /**
     * Pushes onto the stack a luatable with key-value pairs from the collection
     *
     * <p>The keys of the luatable starts from 1.</p>
     */
    public void push(Collection<?> array) {
        lua_createtable(L, array.size(), 0);
        int i = 1;
        for (Object o :
                array) {
            push(o);
            lua_rawseti(L, -2, i);
            i++;
        }
    }

    /**
     * Pushes element onto the stack with {@link Lua.Conversion#FULL}
     *
     * <p>Converts the element to lua types automatically.</p>
     *
     * @see Lua.Conversion#FULL
     * @param obj object to be converted and pushed onto the stack
     */
    public void push(Object obj) {
        pushJava(obj, Lua.Conversion.FULL);
    }

    /**
     * Pushes element onto the stack with specified degree of {@link Lua.Conversion}
     * @param obj object to be converted and pushed onto the stack
     * @param degree the conversion degree
     * @see Lua.Conversion
     */
    public void pushJava(Object obj, Lua.Conversion degree) {
        if (obj == null) {
            lua_pushnil(L);
        } else if (degree == Lua.Conversion.NONE) {
            pushJavaObjectOrArray(obj);
        } else {
            if (obj instanceof Boolean) {
                push((boolean) obj);
            } else if (obj instanceof String) {
                push((String) obj);
            } else if (obj instanceof Integer || obj instanceof Long ||
                    obj instanceof Byte || obj instanceof Short) {
                push(((Number) obj).longValue());
            } else if (obj instanceof Number) {
                push((Number) obj);
            } else if (degree == Lua.Conversion.SEMI) {
                pushJavaObjectOrArray(obj);
            } else /* (degree == Conversion.FULL) */ {
                if (obj instanceof Map) {
                    push((Map<?, ?>) obj);
                } else if (obj instanceof Collection) {
                    push((Collection<?>) obj);
                } else if (obj.getClass().isArray()) {
                    push(obj, true);
                } else {
                    pushJavaObject(obj);
                }
            }
        }
    }

    /**
     * Pushes the Java object element onto lua stack with a metatable
     * that reflects fields and method calls
     */
    public void pushJavaObject(@NotNull Object obj) {
        jniPushJavaObject(L, obj);
    }

    /**
     * Pushes the Java array element onto lua stack with a metatable
     * that reflects element lookups and assignments
     *
     * @param arr the array (primitive or not)
     */
    public void pushJavaArray(@NotNull Object arr) {
        jniPushJavaArray(L, arr);
    }

    /**
     * A convenient method
     * @param obj object or array
     */
    protected void pushJavaObjectOrArray(@NotNull Object obj) {
        if (obj.getClass().isArray()) {
            pushJavaArray(obj);
        } else {
            pushJavaObject(obj);
        }
    }

    /**
     * See {@link JuaJitNatives#lua_toboolean}
     */
    public boolean toBoolean(int index) {
        return lua_toboolean(L, index) == 1;
    }

    /**
     * Converts a wrapped Java object back to Java object
     */
    public Object toJavaObject(int index) {
        return jniToJavaObject(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_tonumber}
     */
    public double toNumber(int index) {
        return lua_tonumber(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_tostring}
     */
    public String toString(int index) {
        return lua_tostring(L, index);
    }

    /**
     * Converts the luatable at the given index to a {@link List} using <code>unpack</code>
     */
    public List<Object> toList(int index) {
        int top = lua_gettop(L);
        if (lua_istable(L, index) == 1) {
            lua_getglobal(L, "unpack");
            lua_pushvalue(L, -2);
            try {
                if (lua_pcall(L, 1, Consts.LUA_MULTRET, 0) == 0) {
                    int len = lua_gettop(L);
                    ArrayList<Object> list = new ArrayList<>();
                    list.ensureCapacity(len - top + 1);
                    for (int i = top + 1; i <= len; ++i) {
                        list.add(toObject(i));
                    }
                    lua_settop(L, top);
                    return list;
                } else {
                    lua_settop(L, top);
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Converts the element at the given index to a corresponding Java type
     *
     * <p>Unsupported types:</p>
     * <ul>
     *     <li>Userdata that are not proxied Java objects</li>
     *     <li>Lightuserdata</li>
     * </ul>
     */
    public Object toObject(int index) {
        int type = lua_type(L, index);
        switch (type) {
            case Consts.LUA_TNIL:
                return null;
            case Consts.LUA_TBOOLEAN:
                return toBoolean(index);
            case Consts.LUA_TNUMBER:
                return toNumber(index);
            case Consts.LUA_TSTRING:
                return toString(index);
            case Consts.LUA_TTABLE:
                return toMap(index);
            case Consts.LUA_TUSERDATA:
                return toJavaObject(index);
        }
        return null;
    }

    public Object toObject(int index, Class<?> type) {
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
        throw new IllegalArgumentException("Unable to convert type");
    }

    /**
     * Converts the luatable at the given index to a {@link Map}
     */
    public Map<Object, Object> toMap(int index) {
        int top = lua_gettop(L);
        if (lua_istable(L, index) == 1) {
            lua_pushnil(L);
            Map<Object, Object> map = new HashMap<>();
            while (lua_next(L, -2) != 0) {
                Object key = toObject(-2);
                if (key != null) {
                    map.put(key, toObject(-1));
                }
                lua_pop(L, 1);
            }
            return map;
        }
        return null;
    }

    /**
     * See {@link JuaJitNatives#lua_gettop}
     */
    public int gettop() {
        return lua_gettop(L);
    }

    /**
     * See {@link JuaJitNatives#lua_settop}
     */
    public void settop(int top) {
        lua_settop(L, top);
    }

    /**
     * See {@link JuaJitNatives#luaL_loadstring}
     */
    public int load(String collect) {
        return luaL_loadstring(L, collect);
    }

    /**
     * See {@link JuaJitNatives#luaL_loadbuffer}
     */
    public int load(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            return luaL_loadbuffer(L, buffer, buffer.limit(), name);
        }
        return -1;
    }

    /**
     * See {@link JuaJitNatives#lua_pcall}
     */
    public int pcall(int nargs, int nresults) {
        return lua_pcall(L, nargs, nresults, 0);
    }

    /**
     * See {@link JuaJitNatives#lua_resume}
     */
    public int resume(int nargs) {
        return lua_resume(L, nargs);
    }

    /**
     * See {@link JuaJitNatives#lua_newthread}
     *
     * <p>Note that this is just a wrapper to {@link JuaJitNatives#lua_newthread(long)},
     * and no reference to this new thread is created yet. So you need to
     * create references to it to prevent lua garbage collecting it, which might
     * very like lead to a crash sometime.</p>
     */
    public Jua newthread() {
        return new Jua(lua_newthread(L), this);
    }

    /**
     * See {@link JuaJitNatives#luaopen_bit}
     */
    public void openBitLibrary() {
        luaopen_bit(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_debug}
     */
    public void openDebugLibrary() {
        luaopen_debug(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_io}
     */
    public void openIOLibrary() {
        luaopen_io(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_math}
     */
    public void openMathLibrary() {
        luaopen_math(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_os}
     */
    public void openOsLibrary() {
        luaopen_os(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_package}
     */
    public void openPackageLibrary() {
        luaopen_package(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_string}
     */
    public void openStringLibrary() {
        luaopen_string(L);
    }

    /**
     * See {@link JuaJitNatives#luaopen_table}
     */
    public void openTableLibrary() {
        luaopen_table(L);
    }

    /**
     * See {@link JuaJitNatives#luaL_ref}
     * <p>
     * This function always uses {@link Consts#LUA_REGISTRYINDEX} as the table
     */
    public int ref() {
        return luaL_ref(L, Consts.LUA_REGISTRYINDEX);
    }

    /**
     * See {@link JuaJitNatives#luaL_unref}
     * <p>
     * This function always uses {@link Consts#LUA_REGISTRYINDEX} as the table
     */
    public void unref(int ref) {
        luaL_unref(L, Consts.LUA_REGISTRYINDEX, ref);
    }

    /**
     * Shortcut of <code>lua_rawgeti(L, Consts.LUA_REGISTRYINDEX, ref);</code>
     */
    public void refget(int ref) {
        lua_rawgeti(L, Consts.LUA_REGISTRYINDEX, ref);
    }

    /**
     * See {@link JuaJitNatives#lua_getglobal}
     */
    public void getglobal(String name) {
        lua_getglobal(L, name);
    }

    /**
     * See {@link JuaJitNatives#lua_setglobal}
     */
    public void setglobal(String name) {
        lua_setglobal(L, name);
    }

    /**
     * See {@link JuaJitNatives#lua_newtable}
     */
    public void newtable() {
        lua_newtable(L);
    }

    /**
     * See {@link JuaJitNatives#lua_gettable}
     */
    public void gettable(int index) {
        lua_gettable(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_settable}
     */
    public void settable(int index) {
        lua_settable(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_getfield(long, int, String)}
     */
    public void getfield(int index, String k) {
        lua_getfield(L, index, k);
    }

    /**
     * See {@link JuaJitNatives#lua_setfield(long, int, String)}
     */
    public void setfield(int index, String k) {
        lua_setfield(L, index, k);
    }

    /**
     * See {@link JuaJitNatives#lua_rawget(long, int)}
     */
    public void rawget(int index) {
        lua_rawget(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_rawset(long, int)}
     */
    public void rawset(int index) {
        lua_rawset(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_rawgeti(long, int, int)}
     */
    public void rawgeti(int index, int n) {
        lua_rawgeti(L, index, n);
    }

    /**
     * See {@link JuaJitNatives#lua_rawseti(long, int, int)}
     */
    public void rawseti(int index, int n) {
        lua_rawseti(L, index, n);
    }

    /**
     * See {@link JuaJitNatives#lua_type}
     */
    public int type(int index) {
        return lua_type(L, index);
    }

    /**
     * See {@link JuaJitNatives#lua_isnil(long, int)}
     */
    public boolean isnil(int index) {
        return lua_isnil(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_isboolean(long, int)}
     */
    public boolean isboolean(int index) {
        return lua_isboolean(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_istable(long, int)}
     */
    public boolean istable(int index) {
        return lua_istable(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_isstring(long, int)}
     */
    public boolean isstring(int index) {
        return lua_isstring(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_isuserdata(long, int)}
     * @param index the stack index
     * @return {@code true} if the element is userdata or lightuserdata
     */
    public boolean isuserdata(int index) {
        return lua_isuserdata(L, index) == 1;
    }

    /**
     * See {@link JuaJitNatives#lua_isnumber(long, int)}
     */
    public boolean isnumber(int index) {
        return lua_isnumber(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_isnone(long, int)}
     */
    public boolean isnone(int index) {
        return lua_isnone(L, index) != 0;
    }

    /**
     * See {@link JuaJitNatives#lua_next(long, int)}
     */
    public int next(int i) {
        return lua_next(L, i);
    }

    /**
     * See {@link JuaJitNatives#lua_pop(long, int)}
     */
    public void pop(int i) {
        lua_pop(L, i);
    }

    /**
     * See {@link JuaJitNatives#lua_pushnil(long)}
     */
    public void pushnil() {
        lua_pushnil(L);
    }

    /**
     * Creates a proxy for a luatable on top of the stack, implementing
     * the interfaces specified in <code>implem</code> param.
     */
    public Object createProxy(String implem) {
        if (lua_istable(L, -1) == 0) {
            pop(1);
            return null;
        } else {
            Class<?>[] classes = JuaAPI.getClasses(implem);
            return Proxy.newProxyInstance(
                    Jua.class.getClassLoader(),
                    classes,
                    new JuaProxy(ref(), this)
            );
        }
    }

    /**
     * Calls {@link #dispose()}
     */
    @Override
    public void close() {
        if (mainThread == this) {
            dispose();
        }
    }

    /**
     * See {@link JuaJitNatives#lua_equal(long, int, int)}.
     *
     * @param i  the stack index of the first element
     * @param i1 the stack index of the second element
     * @return true if the two elements equal
     */
    public boolean equal(int i, int i1) {
        return lua_equal(L, i, i1) == 1;
    }

    /**
     * Sets the function as the global name
     *
     * @param name     the global name
     * @param function the function
     */
    public void register(String name, JFunction function) {
        push(function);
        setglobal(name);
    }

    /**
     * See {@link JuaJitNatives#luaL_openlibs(long)}.
     */
    public void openLibraries() {
        luaL_openlibs(L);
    }

}
