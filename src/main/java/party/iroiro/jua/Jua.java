package party.iroiro.jua;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.DoNotCall;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.nio.Buffer;
import java.util.*;

/**
 * Wrapper of a <code>lua_State *</code> (LuaJIT)
 *
 * <p>The LuaJIT library and the JNI binding is automatically
 * loaded with {@link SharedLibraryLoader} and initialized.</p>
 *
 * <p><code>protected</code> functions that have identical names to lua ones
 * are mostly simply wrapper of the corresponding C API.</p>
 */
public class Jua implements AutoCloseable {
    static {
        new SharedLibraryLoader().load("jua");
        try {
            initBindings();
        } catch (Exception e) {
            throw new RuntimeException("Jua: Class binding failed", e);
        }
    }

    /**
     * The pointer, i.e. the internal <code>lua_State *</code>
     */
    protected final long L;
    protected final int stateIndex;
    protected final Jua mainThread;
    protected final List<Jua> subThreads;

    /*JNI
        #include "lua.hpp"
        #include "jni.h"

        #include "luaexception.h"
        #include "jua.h"
        #include "juaapi.h"
        #include "jualib.h"

        #include "juaamalg.h"
         */

    /**
     * Start caching some classes, methodIDs, etc.
     *
     * <ul>
     *     <li>{@link Class}</li>
     *     <li>{@link Throwable}</li>
     *     <li>{@link JuaAPI}</li>
     * </ul>
     *
     * <p>It is not multi-thread safe and I am already calling it from
     * a static block in this class (yes {@link Jua}).
     * So you probably do not want to call it again somewhere.</p>
     * @throws Exception when <code>FindClass, NewGlobalRef,
     *         GetStaticMethodId</code>, etc. errs
     */
    protected static native void initBindings() throws Exception; /*
        try {
            initBindings(env);
        } catch (LuaException const &e) {
            // Java-side exceptions are not cleared if any
            return;
        }
    */

    /* luaL_Buffer relevant API is ignored as we have StringBuilder */

    /**
     * Wraps <code>luaL_callmeta</code>:
     *
     * <p>Calls a metamethod.</p>
     * <p>If the object at index obj has a metatable and this metatable
     * has a field e, this function calls this field and passes the
     * object as its only argument. In this case this function returns
     * 1 and pushes onto the stack the value returned by the call. If
     * there is no metatable or no metamethod, this function returns
     * 0 (without pushing any value on the stack).</p>
     */
    @CheckReturnValue
    protected static native int luaL_callmeta(long ptr, int obj, String e); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_callmeta(L, (int) obj, (const char *) e);
    */

    /**
     * Wraps <code>luaL_dostring</code>:
     *
     * <p>Loads and runs the given string. It is defined as the following
     * macro:
     * <pre><code>(luaL_loadstring(L, str) || lua_pcall(L, 0, LUA_MULTRET, 0))</code></pre>
     * </p>
     * <p>It returns 0 if there are no errors or 1 in case of errors.</p>
     */
    @CheckReturnValue
    protected static native int luaL_dostring(long ptr, String str); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_dostring(L, (const char *) str);
    */

    /**
     * Wraps <code>luaL_getmetafield</code>:
     *
     * <p>Pushes onto the stack the field e from the metatable of the object
     * at index obj. If the object does not have a metatable, or if
     * the metatable does not have this field, returns 0 and pushes
     * nothing.</p>
     */
    @CheckReturnValue
    protected static native int luaL_getmetafield(long ptr, int obj, String e); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_getmetafield(L, (int) obj, (const char *) e);
    */

    /**
     * Wraps <code>luaL_getmetatable</code>:
     *
     * <p>Pushes onto the stack the metatable associated with name tname
     * in the registry (see {@link #luaL_newmetatable}).</p>
     */
    protected static native void luaL_getmetatable(long ptr, String tname); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaL_getmetatable(L, (const char *) tname);
    */

    /**
     * Wraps <code>luaL_loadbuffer</code>:
     *
     * <p>Loads a buffer as a Lua chunk. This function uses
     * <a href="https://www.lua.org/manual/5.1/manual.html#lua_load">
     *     lua_load</a> to load the chunk in the buffer pointed
     * to by buff with size sz.</p>
     * <p>This function returns the same results as lua_load. name is the
     * chunk name, used for debug information and error messages.</p>
     *
     * <p>The return values of lua_load are:
     * <ul>
     *     <li>0: no errors;</li>
     *     <li>{@link Consts#LUA_ERRSYNTAX}: syntax error during pre-compilation;</li>
     *     <li>{@link Consts#LUA_ERRMEM}: memory allocation error.</li>
     * </ul></p>
     *
     * <p>It assumes the buffer {@link Buffer#isDirect()}</p>
     */
    @CheckReturnValue
    protected static native int luaL_loadbuffer(long ptr, Buffer buff, long sz, String name); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_loadbuffer(L, (const char *) buff,
                                      (size_t) sz, (const char *) name);
    */

    /**
     * Wraps <code>luaL_loadstring</code>:
     *
     * <p>Loads a string as a Lua chunk. This function uses lua_load to
     * load the chunk in the zero-terminated string s.
     * This function returns the same results as lua_load.</p>
     * <p>Also as <a href="https://www.lua.org/manual/5.1/manual.html#lua_load">lua_load</a>,
     * this function only loads the chunk; it does not run it.</p>
     */
    @CheckReturnValue
    protected static native int luaL_loadstring(long ptr, String s); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_loadstring(L, (const char *) s);
    */

    /**
     * Wraps <code>luaL_newmetatable</code>:
     *
     * <p>If the registry already has the key tname, returns 0.</p>
     * <p>Otherwise, creates a new table to be used as a metatable for
     * userdata, adds it to the registry with key tname, and returns
     * 1.</p>
     * <p>In both cases pushes onto the stack the final value associated
     * with tname in the registry.</p>
     */
    @CheckReturnValue
    protected static native int luaL_newmetatable(long ptr, String tname); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_newmetatable(L, (const char *) tname);
    */

    /**
     * Calls <code>luaL_newstate()</code> and initializes the state:
     *
     * <ul>
     *     <li>Registers the <code>index</code> param in lua registry</li>
     *     <li>Allocates space for <code>JNIEnv *</code> storage (updated every call to lua)</li>
     *     <li>Opens the basic (including coroutine) and jit libraries</li>
     * </ul>
     * @param index an identifier to the state
     * @return the <code>lua_State *</code> pointer
     */
    @CheckReturnValue
    protected static native long luaL_newstate(int index); /*
        lua_State* L = luaL_newstate();
        lua_atpanic(L, &fatalError);
        // luaL_openlibs(L);
        luaJ_openlib(L, "", &luaopen_base);
        luaJ_openlib(L, LUA_JITLIBNAME, &luaopen_jit);
        luaJ_openlib(L, LUA_JAVALIBNAME, &luaopen_jua);

        lua_pushstring(L, JAVA_STATE_INDEX);
        lua_pushinteger(L, index);
        lua_settable(L, LUA_REGISTRYINDEX);

        lua_pushstring(L, JNIENV_INDEX);
        JNIEnv ** udEnv = (JNIEnv **) lua_newuserdata(L, sizeof(JNIEnv *));
        *udEnv = env;
        lua_rawset(L, LUA_REGISTRYINDEX);

        initMetaRegistry(L);

        return (jlong) L;
    */

    /**
     * Wraps <code>luaL_openlibs</code>:
     *
     * <p>Opens all standard Lua libraries into the given state.</p>
     */
    protected static native void luaL_openlibs(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaL_openlibs(L);
    */

    /**
     * <code>luaopen_base</code>
     * <p>Loaded by default</p>
     * <p>Also loads the coroutine library</p>
     */
    @DoNotCall @Deprecated
    protected static native void luaopen_base(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, "", &luaopen_base);
    */

    /**
     * <code>luaopen_package</code>
     */
    protected static native void luaopen_package(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_LOADLIBNAME, &luaopen_package);
    */

    /**
     * <code>luaopen_table</code>
     */
    protected static native void luaopen_table(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_TABLIBNAME, &luaopen_table);
    */

    /**
     * <code>luaopen_io</code>
     */
    protected static native void luaopen_io(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_IOLIBNAME, &luaopen_io);
    */

    /**
     * <code>luaopen_os</code>
     */
    protected static native void luaopen_os(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_OSLIBNAME, &luaopen_os);
    */

    /**
     * <code>luaopen_string</code>
     */
    protected static native void luaopen_string(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_STRLIBNAME, &luaopen_string);
    */

    /**
     * <code>luaopen_math</code>
     */
    protected static native void luaopen_math(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_MATHLIBNAME, &luaopen_math);
    */

    /**
     * <code>luaopen_debug</code>
     */
    protected static native void luaopen_debug(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_DBLIBNAME, &luaopen_debug);
    */

    /**
     * <code>luaopen_bit</code>
     */
    protected static native void luaopen_bit(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_BITLIBNAME, &luaopen_bit);
    */

    /**
     * <code>luaopen_jit</code>
     * <p>Loaded by default</p>
     */
    @DoNotCall @Deprecated
    protected static native void luaopen_jit(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaJ_openlib(L, LUA_JITLIBNAME, &luaopen_jit);
    */

    /**
     * Wraps <code>luaL_ref</code>:
     *
     * <p>Creates and returns a reference, in the table at index t, for
     * the object at the top of the stack (and pops the object).</p>
     * <p>A reference is a unique integer key. As long as you do not manually
     * add integer keys into table t, luaL_ref ensures the uniqueness
     * of the key it returns. You can retrieve an object referred by
     * reference r by calling {@link #lua_rawgeti}(L, t, r). Function luaL_unref
     * frees a reference and its associated object.</p>
     * <p>If the object at the top of the stack is nil, luaL_ref returns
     * the constant {@link Consts#LUA_REFNIL}. The constant @link Consts.#LUA_NOREF} is guaranteed
     * to be different from any reference returned by luaL_ref.</p>
     */
    @CheckReturnValue
    protected static native int luaL_ref(long ptr, int t); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) luaL_ref(L, (int) t);
    */

    /**
     * Wraps <code>luaL_register</code>:
     *
     * <p>Opens a library.</p>
     * <p>When called with libname equal to NULL, it simply registers all
     * functions in the list l (see luaL_Reg) into the table on the
     * top of the stack.</p>
     * <p>When called with a non-null libname, luaL_register creates a
     * new table t, sets it as the value of the global variable
     * libname, sets it as the value of package.loaded[libname], and
     * registers on it all functions in the list l. If there is a table
     * in package.loaded[libname] or in variable libname, reuses this
     * table instead of creating a new one.</p>
     * <p>In any case the function leaves the table on the top of the
     * stack.</p>
     */
    @DoNotCall @Deprecated
    protected static native void luaL_register(long ptr, String libname, long l); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaL_register(L, (const char *) libname, (const luaL_Reg *) l);
    */

    /**
     * Wraps <code>luaL_typename</code>:
     *
     * <p>Returns the name of the type of the value at the given
     * index.</p>
     */
    @CheckReturnValue
    protected static native String luaL_typename(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return env->NewStringUTF(luaL_typename(L, (int) index));
    */

    /**
     * Wraps <code>luaL_unref</code>:
     *
     * <p>Releases reference ref from the table at index t (see
     * {@link #luaL_ref}). The entry is removed from the table, so that the referred
     * object can be collected. The reference ref is also freed to be
     * used again.</p>
     * <p>If ref is {@link Consts#LUA_NOREF} or {@link Consts#LUA_REFNIL}, luaL_unref does nothing.</p>
     */
    protected static native void luaL_unref(long ptr, int t, int ref); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaL_unref(L, (int) t, (int) ref);
    */

    /**
     * Wraps <code>luaL_where</code>:
     *
     * <p>Pushes onto the stack a string identifying the current position
     * of the control at level lvl in the call stack. Typically this
     * string has the following format:</p>
     * <code>chunkname:currentline:</code>
     * <p>Level 0 is the running function, level 1 is the function that
     * called the running function, etc.</p>
     * <p>This function is used to build a prefix for error messages.</p>
     */
    protected static native void luaL_where(long ptr, int lvl); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        luaL_where(L, (int) lvl);
    */


    /**
     * Please use {@link #lua_pcall} instead
     *
     * <p>To call a function you must use the following protocol: first, the function
     * to be called is pushed onto the stack; then, the arguments to the function
     * are pushed in direct order; that is, the first argument is pushed first.
     * Finally you call lua_call; nargs is the number of arguments that you pushed onto
     * the stack. All arguments and the function value are popped from the stack when
     * the function is called. The function results are pushed onto the stack when the
     * function returns. The number of results is adjusted to nresults, unless nresults is
     * LUA_MULTRET. In this case, all results from the function are pushed. Lua takes care
     * that the returned values fit into the stack space. The function results are pushed
     * onto the stack in direct order (the first result is pushed first), so that after
     * the call the last result is on the top of the stack.</p>
     *
     * <p>Any error inside the called function is propagated upwards (with a longjmp). </p>
     *
     * @throws UnsupportedOperationException always
     */
    @SuppressWarnings("unused")
    @DoNotCall @Deprecated
    protected static void lua_call(long ptr, int nargs, int nresults) {
        throw new UnsupportedOperationException("Please use lua_pcall instead");
    }

    /**
     * Wrapper of C <code>lua_close</code>
     *
     * Destroys all objects in the given Lua state
     * (calling the corresponding garbage-collection metamethods, if any)
     * and frees all dynamic memory used by this state.
     * @param ptr the lua_State pointer
     * @see <a href=https://www.lua.org/manual/5.1/manual.html#lua_close>lua_close</a>
     */
    protected static native void lua_close(long ptr); /*
        lua_close((lua_State *) ptr);
    */

    /**
     * Concatenates the n values at the top of the stack, pops them, and leaves the result at the top.
     * @param ptr the state pointer
     * @param n <code>n</code>
     */
    protected static native void lua_concat(long ptr, int n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_concat(L, (int) n);
    */

    /**
     * Creates a new empty table and pushes it onto the stack. The new table has
     * space pre-allocated for narr array elements and nrec non-array elements.
     */
    protected static native void lua_createtable(long ptr, int narr, int nrec); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_createtable(L, (int) narr, (int) nrec);
    */

    /**
     * Returns 1 if the two values in acceptable indices index1 and index2 are equal,
     * following the semantics of the Lua == operator (that is, may call metamethods).
     * Otherwise returns 0. Also returns 0 if any of the indices is non valid.
     */
    @CheckReturnValue
    protected static native int lua_equal(long ptr, int index1, int index2); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_equal(L, (int) index1, (int) index2);
    */

    /**
     * Raw use of lua_error longjmp is not supported
     */
    @SuppressWarnings("unused")
    @DoNotCall @Deprecated
    protected static void lua_error(long ptr) {
        throw new UnsupportedOperationException("Raw use of lua_error longjmp is not supported");
    }

    /**
     * Controls the garbage collector.
     *
     * Copied from Lua doc:
     * <ul>
     *     <li>{@link Consts#LUA_GCSTOP}: stops the garbage collector.</li>
     *     <li>{@link Consts#LUA_GCRESTART}: restarts the garbage collector.</li>
     *     <li>{@link Consts#LUA_GCCOLLECT}: performs a full garbage-collection cycle.</li>
     *     <li>{@link Consts#LUA_GCCOUNT}: returns the current amount of memory (in Kbytes) in use by Lua.</li>
     *     <li>{@link Consts#LUA_GCCOUNTB}: returns the remainder of dividing the current amount of bytes of
     *     memory in use by Lua by 1024.</li>
     *     <li>{@link Consts#LUA_GCSTEP}: performs an incremental step of garbage collection.
     *     The step "size" is controlled by data (larger values mean more steps)
     *     in a non-specified way. If you want to control the step size you must
     *     experimentally tune the value of data. The function returns 1 if the step
     *     finished a garbage-collection cycle.</li>
     *     <li>{@link Consts#LUA_GCSETPAUSE}: sets data as the new value for the pause of the
     *     collector (see §2.10). The function returns the previous value of the pause.</li>
     *     <li>{@link Consts#LUA_GCSETSTEPMUL}: sets data as the new value for the step multiplier of
     *     the collector (see §2.10). The function returns the previous value of the step multiplier.</li>
     * </ul>
     * @param ptr the state pointer
     * @param what see desciption
     * @param data depends on <code>what</code>
     * @return depends on <code>what</code>
     */
    @CheckReturnValue
    protected static native int lua_gc(long ptr, int what, int data); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return lua_gc(L, (int) what, (int) data);
    */

    /**
     * Pushes onto the stack the environment table of the value at the given index.
     */
    protected static native void lua_getfenv(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_getfenv(L, (int) index);
    */

    /**
     * Pushes onto the stack the value t[k], where t is the value at the given valid index.
     *
     * <p>As in Lua, this function may trigger a metamethod for the "index" event</p>
     */
    protected static native void lua_getfield(long ptr, int index, String k); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_getfield(L, (int) index, k);
    */

    /**
     * <p>Pushes onto the stack the value of the global name.</p>
     */
    protected static native void lua_getglobal(long ptr, String name); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_getglobal(L, name);
    */

    /**
     * Pushes onto the stack the metatable of the value at the given acceptable index.
     * <p>If the index is not valid, or if the value does not have a metatable,
     * the function returns 0 and pushes nothing on the stack.</p>
     * @return 0 if unexpected
     */
    @CheckReturnValue
    protected static native int lua_getmetatable(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return lua_getmetatable(L, (int) index);
    */

    /**
     * Pushes onto the stack the value t[k], where t is the value at the given valid index
     * and k is the value at the top of the stack.
     * This function pops the key from the stack (putting the resulting value in its place).
     * As in Lua, this function may trigger a metamethod for the "index" event
     */
    protected static native void lua_gettable(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_gettable(L, (int) index);
    */

    /**
     * Returns the index of the top element in the stack. Because indices start at 1,
     * this result is equal to the number of elements in the stack (and so 0 means an empty stack).
     * @return the stack size
     */
    @CheckReturnValue
    protected static native int lua_gettop(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_gettop(L);
    */

    /**
     * Wraps <code>lua_insert</code>:
     *
     * <p>Moves the top element into the given valid index, shifting up
     * the elements above this index to open space. Cannot be called
     * with a pseudo-index, because a pseudo-index is not an actual
     * stack position.</p>
     */
    protected static native void lua_insert(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_insert(L, (int) index);
    */

    /**
     * Wraps <code>lua_isboolean</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index has type
     * boolean, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isboolean(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isboolean(L, (int) index);
    */

    /**
     * Wraps <code>lua_iscfunction</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a C
     * function, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_iscfunction(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_iscfunction(L, (int) index);
    */

    /**
     * Wraps <code>lua_isfunction</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a function
     * (either C or Lua), and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isfunction(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isfunction(L, (int) index);
    */

    /**
     * Wraps <code>lua_islightuserdata</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a light
     * userdata, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_islightuserdata(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_islightuserdata(L, (int) index);
    */

    /**
     * Wraps <code>lua_isnil</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is nil,
     * and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isnil(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isnil(L, (int) index);
    */

    /**
     * Wraps <code>lua_isnone</code>:
     *
     * <p>Returns 1 if the given acceptable index is not valid (that
     * is, it refers to an element outside the current stack), and 0
     * otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isnone(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isnone(L, (int) index);
    */

    /**
     * Wraps <code>lua_isnoneornil</code>:
     *
     * <p>Returns 1 if the given acceptable index is not valid (that
     * is, it refers to an element outside the current stack) or if
     * the value at this index is nil, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isnoneornil(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isnoneornil(L, (int) index);
    */

    /**
     * Wraps <code>lua_isnumber</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a number
     * or a string convertible to a number, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isnumber(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isnumber(L, (int) index);
    */

    /**
     * Wraps <code>lua_isstring</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a string
     * or a number (which is always convertible to a string), and 0
     * otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isstring(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isstring(L, (int) index);
    */

    /**
     * Wraps <code>lua_istable</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a
     * table, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_istable(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_istable(L, (int) index);
    */

    /**
     * Wraps <code>lua_isthread</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a
     * thread, and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isthread(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isthread(L, (int) index);
    */

    /**
     * Wraps <code>lua_isuserdata</code>:
     *
     * <p>Returns 1 if the value at the given acceptable index is a userdata
     * (either full or light), and 0 otherwise.</p>
     */
    @CheckReturnValue
    protected static native int lua_isuserdata(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_isuserdata(L, (int) index);
    */

    /**
     * Wraps <code>lua_lessthan</code>:
     *
     * <p>Returns 1 if the value at acceptable index index1 is smaller
     * than the value at acceptable index index2, following the semantics
     * of the Lua &lt; operator (that is, may call metamethods). Otherwise
     * returns 0. Also returns 0 if any of the indices is non
     * valid.</p>
     */
    @CheckReturnValue
    protected static native int lua_lessthan(long ptr, int index1, int index2); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_lessthan(L, (int) index1, (int) index2);
    */

    /**
     * Wraps <code>lua_newtable</code>:
     *
     * <p>Creates a new empty table and pushes it onto the stack. It is
     * equivalent to {@link #lua_createtable}(L, 0, 0).</p>
     */
    protected static native void lua_newtable(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_newtable(L);
    */

    /**
     * Wraps <code>lua_newthread</code>:
     *
     * <p>Creates a new thread, pushes it on the stack, and returns a pointer
     * to a lua_State that represents this new thread. The new state
     * returned by this function shares with the original state all
     * global objects (such as tables), but has an independent execution
     * There is no explicit function to close or to destroy a
     * thread. Threads are subject to garbage collection, like any Lua
     * object.</p>
     *
     * <p>Using the new thread and the old lua state in different OS threads,
     * of course, may lead to unexpected behaviours or even crash.</p>
     */
    @CheckReturnValue
    protected static native long lua_newthread(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_newthread(L);
    */

    /**
     * Wraps <code>lua_newuserdata</code>:
     *
     * <p>This function allocates a new block of memory with the given
     * size, pushes onto the stack a new full userdata with the block
     * address, and returns this address.</p>
     * <p>Userdata represent C values in Lua. A full userdata represents
     * a block of memory. It is an object (like a table): you must create
     * it, it can have its own metatable, and you can detect when it
     * is being collected. A full userdata is only equal to itself
     * (under raw equality).</p>
     * <p>When Lua collects a full userdata with a gc metamethod, Lua calls
     * the metamethod and marks the userdata as finalized. When this
     * userdata is collected again then Lua frees its corresponding
     * memory.</p>
     */
    @CheckReturnValue
    protected static native long lua_newuserdata(long ptr, long size); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_newuserdata(L, (size_t) size);
    */

    /**
     * Wraps <code>lua_next</code>:
     *
     * <p>Pops a key from the stack, and pushes a key-value pair from the
     * table at the given index (the "next" pair after the given
     * key). If there are no more elements in the table, then lua_next
     * returns 0 (and pushes nothing).</p>
     *
     * <p>Note: to start, you may use nil as the key.</p>
     */
    @CheckReturnValue
    protected static native int lua_next(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_next(L, (int) index);
    */

    /**
     * Wraps <code>lua_objlen</code>:
     *
     * Returns the "length" of the value at the given acceptable
     * index: for strings, this is the string length; for tables, this
     * is the result of the length operator ('#'); for userdata, this
     * is the size of the block of memory allocated for the
     * userdata; for other values, it is 0.
     */
    @CheckReturnValue
    protected static native long lua_objlen(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_objlen(L, (int) index);
    */

    /**
     * Wraps <code>lua_pcall</code>:
     *
     * <p>Calls a function in protected mode.</p>
     * <p>Both nargs and nresults have the same meaning as in
     * lua_call. If there are no errors during the call, lua_pcall behaves
     * exactly like {@link #lua_call}. However, if there is any error, lua_pcall
     * catches it, pushes a single value on the stack (the error
     * message), and returns an error code. Like lua_call, lua_pcall
     * always removes the function and its arguments from the
     * stack.</p>
     * <p>If errfunc is 0, then the error message returned on the stack
     * is exactly the original error message. Otherwise, errfunc is
     * the stack index of an error handler function. (In the current
     * implementation, this index cannot be a pseudo-index.) In case
     * of runtime errors, this function will be called with the error
     * message and its return value will be the message returned on
     * the stack by lua_pcall.</p>
     * <p>Typically, the error handler function is used to add more debug
     * information to the error message, such as a stack traceback.
     * Such information cannot be gathered after the return of
     * lua_pcall, since by then the stack has unwound.</p>
     * <p>The lua_pcall function returns 0 in case of success or one of
     * the following error codes (defined in lua.h):
     * <ul>
     * <li>{@link Consts#LUA_ERRRUN}: a runtime error.</li>
     * <li>{@link Consts#LUA_ERRMEM}: memory allocation error. For such errors, Lua
     *     does not call the error handler function.</li>
     * <li>{@link Consts#LUA_ERRERR}: error while running the error handler
     *     function.</li>
     * </ul>
     * </p>
     */
    @CheckReturnValue
    protected static native int lua_pcall(long ptr, int nargs, int nresults, int errfunc); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_pcall(L, (int) nargs, (int) nresults, (int) errfunc);
    */

    /**
     * Wraps <code>lua_pop</code>:
     *
     * Pops n elements from the stack.
     */
    protected static native void lua_pop(long ptr, int n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pop(L, (int) n);
    */

    /**
     * Wraps <code>lua_pushboolean</code>:
     *
     * Pushes a boolean value with value b onto the stack.
     */
    protected static native void lua_pushboolean(long ptr, int b); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushboolean(L, (int) b);
    */

    /**
     * Wraps <code>lua_pushcclosure</code>:
     *
     * Pushes a new C closure onto the stack.
     * When a C function is created, it is possible to associate some
     * values with it, thus creating a C closure (see §3.4); these values
     * are then accessible to the function whenever it is called. To
     * associate values with a C function, first these values should
     * be pushed onto the stack (when there are multiple values, the
     * first value is pushed first). Then lua_pushcclosure is called
     * to create and push the C function onto the stack, with the argument
     * n telling how many values should be associated with the
     * function. lua_pushcclosure also pops these values from the
     * stack.
     * The maximum value for n is 255.
     * @throws UnsupportedOperationException always
     */
    @SuppressWarnings("unused")
    @DoNotCall @Deprecated
    protected static void lua_pushcclosure(long ptr, long fn, int n) {
        throw new UnsupportedOperationException("Please don't");
    }

    /**
     * Wraps <code>lua_pushcfunction</code>:
     *
     * Pushes a C function onto the stack. This function receives a
     * pointer to a C function and pushes onto the stack a Lua value
     * of type function that, when called, invokes the corresponding
     * C function.
     * @throws UnsupportedOperationException always
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    @DoNotCall
    protected static void lua_pushcfunction(long ptr, long f) {
        throw new UnsupportedOperationException("Please don't");
    }

    /**
     * Wraps <code>lua_pushinteger</code>:
     *
     * Pushes a number with value n onto the stack.
     */
    protected static native void lua_pushinteger(long ptr, long n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushinteger(L, (lua_Integer) n);
    */

    /**
     * Wraps <code>lua_pushlightuserdata</code>:
     *
     * Pushes a light userdata onto the stack.
     * Userdata represent C values in Lua. A light userdata represents
     * a pointer. It is a value (like a number): you do not create
     * it, it has no individual metatable, and it is not collected
     * (as it was never created). A light userdata is equal to
     * "any" light userdata with the same C address.
     */
    protected static native void lua_pushlightuserdata(long ptr, long p); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushlightuserdata(L, (void *) p);
    */

    /**
     * Wraps <code>lua_pushlstring</code>:
     *
     * Pushes the string pointed to by s with size len onto the
     * stack. Lua makes (or reuses) an internal copy of the given
     * string, so the memory at s can be freed or reused immediately
     * after the function returns. The string can contain embedded
     * zeros.
     */
    protected static native void lua_pushlstring(long ptr, String s, long len); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushlstring(L, (const char *) s, (size_t) len);
    */

    /**
     * Wraps <code>lua_pushnil</code>:
     *
     * Pushes a nil value onto the stack.
     */
    protected static native void lua_pushnil(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushnil(L);
    */

    /**
     * Wraps <code>lua_pushnumber</code>:
     *
     * Pushes a number with value n onto the stack.
     */
    protected static native void lua_pushnumber(long ptr, double n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushnumber(L, (lua_Number) n);
    */

    /**
     * Wraps <code>lua_pushstring</code>:
     *
     * Pushes the zero-terminated string pointed to by s onto the
     * stack. Lua makes (or reuses) an internal copy of the given
     * string, so the memory at s can be freed or reused immediately
     * after the function returns. The string cannot contain embedded
     * zeros; it is assumed to end at the first zero.
     */
    protected static native void lua_pushstring(long ptr, String s); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushstring(L, (const char *) s);
    */

    /**
     * Wraps <code>lua_pushthread</code>:
     *
     * Pushes the thread represented by L onto the stack. Returns 1
     * if this thread is the main thread of its state.
     */
    @CheckReturnValue
    protected static native int lua_pushthread(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_pushthread(L);
    */

    /**
     * Wraps <code>lua_pushvalue</code>:
     *
     * Pushes a copy of the element at the given valid index onto the
     * stack.
     */
    protected static native void lua_pushvalue(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_pushvalue(L, (int) index);
    */

    /**
     * Wraps <code>lua_rawequal</code>:
     *
     * Returns 1 if the two values in acceptable indices index1 and
     * index2 are primitively equal (that is, without calling
     * metamethods). Otherwise returns 0. Also returns 0 if any of the
     * indices are non valid.
     */
    @CheckReturnValue
    protected static native int lua_rawequal(long ptr, int index1, int index2); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_rawequal(L, (int) index1, (int) index2);
    */

    /**
     * Wraps <code>lua_rawget</code>:
     *
     * Similar to {@link #lua_gettable}, but does a raw access (i.e., without
     * metamethods).
     */
    protected static native void lua_rawget(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_rawget(L, (int) index);
    */

    /**
     * Wraps <code>lua_rawgeti</code>:
     *
     * Pushes onto the stack the value t[n], where t is the value at
     * the given valid index. The access is raw; that is, it does not
     * invoke metamethods.
     */
    protected static native void lua_rawgeti(long ptr, int index, int n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_rawgeti(L, (int) index, (int) n);
    */

    /**
     * Wraps <code>lua_rawset</code>:
     *
     * Similar to {@link #lua_settable}, but does a raw assignment (i.e., without
     * metamethods).
     */
    protected static native void lua_rawset(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_rawset(L, (int) index);
    */

    /**
     * Wraps <code>lua_rawseti</code>:
     *
     * Does the equivalent of t[n] = v, where t is the value at the
     * given valid index and v is the value at the top of the
     * stack.
     * This function pops the value from the stack. The assignment is
     * raw; that is, it does not invoke metamethods.
     */
    protected static native void lua_rawseti(long ptr, int index, int n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_rawseti(L, (int) index, (int) n);
    */

    /**
     * Wraps <code>lua_remove</code>:
     *
     * Removes the element at the given valid index, shifting down the
     * elements above this index to fill the gap. Cannot be called with
     * a pseudo-index, because a pseudo-index is not an actual stack
     * position.
     */
    protected static native void lua_remove(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_remove(L, (int) index);
    */

    /**
     * Wraps <code>lua_replace</code>:
     *
     * Moves the top element into the given position (and pops it),
     * without shifting any element (therefore replacing the value at
     * the given position).
     */
    protected static native void lua_replace(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_replace(L, (int) index);
    */

    /**
     * Wraps <code>lua_resume</code>:
     *
     * Starts and resumes a coroutine in a given thread.
     * To start a coroutine, you first create a new thread (see
     * lua_newthread); then you push onto its stack the main function
     * plus any arguments; then you call lua_resume, with narg being
     * the number of arguments. This call returns when the coroutine
     * suspends or finishes its execution. When it returns, the stack
     * contains all values passed to lua_yield, or all values returned
     * by the body function. lua_resume returns {@link Consts#LUA_YIELD} if the coroutine
     * yields, 0 if the coroutine finishes its execution without
     * errors, or an error code in case of errors (see lua_pcall). In
     * case of errors, the stack is not unwound, so you can use the
     * debug API over it. The error message is on the top of the
     * stack. To restart a coroutine, you put on its stack only the
     * values to be passed as results from yield, and then call
     * lua_resume.
     */
    @CheckReturnValue
    protected static native int lua_resume(long ptr, int narg); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_resume(L, (int) narg);
    */

    /**
     * Wraps <code>lua_setfenv</code>:
     *
     * Pops a table from the stack and sets it as the new environment
     * for the value at the given index. If the value at the given index
     * is neither a function nor a thread nor a userdata, lua_setfenv
     * returns 0. Otherwise it returns 1.
     */
    @CheckReturnValue
    protected static native int lua_setfenv(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_setfenv(L, (int) index);
    */

    /**
     * Wraps <code>lua_setfield</code>:
     *
     * Does the equivalent to t[k] = v, where t is the value at the
     * given valid index and v is the value at the top of the
     * stack.
     * This function pops the value from the stack. As in Lua, this
     * function may trigger a metamethod for the "newindex" event.
     */
    protected static native void lua_setfield(long ptr, int index, String k); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_setfield(L, (int) index, (const char *) k);
    */

    /**
     * Wraps <code>lua_setglobal</code>:
     *
     * Pops a value from the stack and sets it as the new value of global
     * name.
     */
    protected static native void lua_setglobal(long ptr, String name); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_setglobal(L, (const char *) name);
    */

    /**
     * Wraps <code>lua_setmetatable</code>:
     *
     * <p>Pops a table from the stack and sets it as the new metatable
     * for the value at the given acceptable index.</p>
     *
     * <p>As of Lua 5.4, "(For historical reasons, this function returns an int, which now is always 1.)".</p>
     */
    @CheckReturnValue
    protected static native int lua_setmetatable(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_setmetatable(L, (int) index);
    */

    /**
     * Wraps <code>lua_settable</code>:
     *
     * Does the equivalent to t[k] = v, where t is the value at the
     * given valid index, v is the value at the top of the stack, and
     * k is the value just below the top.
     * This function pops both the key and the value from the
     * stack. As in Lua, this function may trigger a metamethod for
     * the "newindex" event
     */
    protected static native void lua_settable(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_settable(L, (int) index);
    */

    /**
     * Wraps <code>lua_settop</code>:
     *
     * Accepts any acceptable index, or 0, and sets the stack top to
     * this index. If the new top is larger than the old one, then the
     * new elements are filled with nil. If index is 0, then all stack
     * elements are removed.
     */
    protected static native void lua_settop(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_settop(L, (int) index);
    */

    /**
     * Wraps <code>lua_status</code>:
     *
     * Returns the status of the thread L.
     * The status can be 0 for a normal thread, an error code if the
     * thread finished its execution with an error, or {@link Consts#LUA_YIELD} if
     * the thread is suspended.
     */
    @CheckReturnValue
    protected static native int lua_status(long ptr); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_status(L);
    */

    /**
     * Wraps <code>lua_toboolean</code>:
     *
     * Converts the Lua value at the given acceptable index to a C boolean
     * value (0 or 1). Like all tests in Lua, lua_toboolean returns
     * 1 for any Lua value different from false and nil; otherwise it
     * returns 0. It also returns 0 when called with a non-valid
     * index. (If you want to accept only actual boolean values, use
     * lua_isboolean to test the value's type.)
     */
    @CheckReturnValue
    protected static native int lua_toboolean(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_toboolean(L, (int) index);
    */

    /**
     * Wraps <code>lua_tointeger</code>:
     *
     * Converts the Lua value at the given acceptable index to the signed
     * integral type lua_Integer. The Lua value must be a number or
     * a string convertible to a number (see §2.2.1); otherwise, lua_tointeger
     * returns 0.
     * If the number is not an integer, it is truncated in some
     * non-specified way.
     */
    @CheckReturnValue
    protected static native long lua_tointeger(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_tointeger(L, (int) index);
    */

    /**
     * Wraps <code>lua_tonumber</code>:
     *
     * Converts the Lua value at the given acceptable index to the C
     * type lua_Number (see lua_Number). The Lua value must be a number
     * or a string convertible to a number (see §2.2.1); otherwise,
     * lua_tonumber returns 0.
     */
    @CheckReturnValue
    protected static native double lua_tonumber(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jdouble) lua_tonumber(L, (int) index);
    */

    /**
     * Wraps <code>lua_topointer</code>:
     *
     * Converts the value at the given acceptable index to a generic
     * C pointer (void*). The value can be a userdata, a table, a
     * thread, or a function; otherwise, lua_topointer returns
     * NULL. Different objects will give different pointers. There is
     * no way to convert the pointer back to its original value.
     * Typically this function is used only for debug information.
     */
    @CheckReturnValue
    protected static native long lua_topointer(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_topointer(L, (int) index);
    */

    /**
     * Wraps <code>lua_tostring</code>:
     *
     * Equivalent to lua_tolstring with len equal to NULL.
     * @return converted Java {@link String}
     */
    @CheckReturnValue
    protected static native String lua_tostring(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        const char * s = lua_tolstring(L, (int) index, NULL);
        jstring str = env->NewStringUTF(s);
        return str;
    */

    /**
     * Wraps <code>lua_tothread</code>:
     *
     * Converts the value at the given acceptable index to a Lua thread
     * (represented as lua_State*). This value must be a thread;
     * otherwise, the function returns NULL.
     */
    @CheckReturnValue
    protected static native long lua_tothread(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_tothread(L, (int) index);
    */

    /**
     * Wraps <code>lua_touserdata</code>:
     *
     * If the value at the given acceptable index is a full
     * userdata, returns its block address. If the value is a light
     * userdata, returns its pointer. Otherwise, returns NULL.
     */
    @CheckReturnValue
    protected static native long lua_touserdata(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jlong) lua_touserdata(L, (int) index);
    */

    /**
     * Wraps <code>lua_type</code>:
     *
     * Returns the type of the value in the given acceptable index,
     * or LUA_TNONE for a non-valid index (that is, an index to an
     * "empty" stack position). The types returned by lua_type are coded
     * by the following constants defined in lua.h: {@link Consts#LUA_TNIL},
     * {@link Consts#LUA_TNUMBER}, {@link Consts#LUA_TBOOLEAN}, {@link Consts#LUA_TSTRING},
     * {@link Consts#LUA_TTABLE}, {@link Consts#LUA_TFUNCTION}, {@link Consts#LUA_TUSERDATA},
     * {@link Consts#LUA_TTHREAD}, and {@link Consts#LUA_TLIGHTUSERDATA}.
     */
    @CheckReturnValue
    protected static native int lua_type(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        return (jint) lua_type(L, (int) index);
    */

    /**
     * Wraps <code>lua_typename</code>:
     *
     * Returns the name of the type encoded by the value tp, which must
     * be one the values returned by lua_type.
     */
    @CheckReturnValue
    protected static native String lua_typename(long ptr, int tp); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        const char * name = lua_typename(L, (int) tp);
        jstring str = env->NewStringUTF(name);
        return str;
    */

    /**
     * Wraps <code>lua_xmove</code>:
     *
     * Exchange values between different threads of the same global
     * state.
     * This function pops n values from the stack from, and pushes them
     * onto the stack to.
     * TODO: update JNIEnv?
     */
    protected static native void lua_xmove(long ptr, long to, int n); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        lua_xmove(L, (lua_State *) to, (int) n);
    */

    protected static native void jniPushJavaObject(long ptr, Object obj); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        jobject global = env->NewGlobalRef(obj);
        if (global != NULL) {
            pushJ<JAVA_OBJECT_META_REGISTRY>(L, global);
        }
    */

    protected static native Object jniToJavaObject(long ptr, int index); /*
        lua_State * L = (lua_State *) ptr;
        updateJNIEnv(env, L);
        // Lua 5.2 / LuaJIT API
        void * p = luaL_testudata(L, index, JAVA_OBJECT_META_REGISTRY);
        if (p == NULL) {
            p = luaL_testudata(L, index, JAVA_CLASS_META_REGISTRY);
        }
        if (p == NULL) {
            p = luaL_testudata(L, index, JAVA_ARRAY_META_REGISTRY);
        }
        if (p == NULL) {
            return NULL;
        } else {
            return *((jobject *) p);
        }
    */

    private static final ArrayList<Jua> luaInstances = new ArrayList<>();
    public static Jua get(int i) {
        synchronized (luaInstances) {
            return luaInstances.get(i);
        }
    }

    public Jua() {
        synchronized (luaInstances) {
            stateIndex = luaInstances.size();
            this.L = luaL_newstate(stateIndex);
            luaInstances.add(this);
            subThreads = new LinkedList<>();
            mainThread = this;
        }
    }

    protected Jua(long L, Jua main) {
        synchronized (luaInstances) {
            stateIndex = luaInstances.size();
            this.L = L;
            luaInstances.add(this);
            subThreads = null;
            mainThread = main;
            mainThread.subThreads.add(this);
        }
    }

    public void dispose() {
        if (mainThread == this) {
            synchronized (luaInstances) {
                for (Jua thread : subThreads) {
                    luaInstances.set(thread.stateIndex, null);
                }
                luaInstances.set(stateIndex, null);
                lua_close(L);
            }
        }
    }

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

    public void push(Map<?, ?> map) {
        lua_createtable(L, 0, map.size());
        map.forEach((k, v) -> {
            push(k);
            push(v);
            lua_rawset(L, -3);
        });
    }

    public void push(Object array, boolean isArray) {
        assert isArray;
        int len = Array.getLength(array);
        lua_createtable(L, len, 0);
        for (int i = 0; i != len; ++i) {
            push(Array.get(array, i));
            lua_rawseti(L, -2, i+1);
        }
    }

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
     * Pushes element onto the stack
     *
     * <p>Converts the element to lua types automatically:
     * <ul>
     *     <li>Boolean -> boolean</li>
     *     <li>String -> string</li>
     *     <li>Number -> lua_Number</li>
     *     <li>Object -> Java object wrapped by a metatable {@link #pushJavaObject}</li>
     * </ul>
     * </p>
     */
    public void push(Object obj) {
        if (obj == null) {
            lua_pushnil(L);
        } else if (obj instanceof Boolean) {
            push((boolean) obj);
        } else if (obj instanceof String) {
            push((String) obj);
        } else if (obj instanceof Integer || obj instanceof Long ||
                obj instanceof Byte || obj instanceof Short) {
            push(((Number) obj).longValue());
        } else if (obj instanceof Number) {
            push((Number) obj);
        } else if (obj instanceof Map) {
            push((Map<?, ?>) obj);
        } else if (obj instanceof Collection) {
            push((Collection<?>) obj);
        } else if (obj.getClass().isArray()) {
            push(obj, true);
        } else {
            pushJavaObject(obj);
        }
    }

    /**
     * Pushes the Java object element onto lua stack with a metatable
     * that reflects fields and method calls
     */
    public void pushJavaObject(Object obj) {
        jniPushJavaObject(L, obj);
    }

    /**
     * See {@link #lua_toboolean}
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
     * See {@link #lua_tonumber}
     */
    public double toNumber(int index) {
        return lua_tonumber(L, index);
    }

    /**
     * See {@link #lua_tostring}
     */
    public String toString(int index) {
        return lua_tostring(L, index);
    }

    public List<Object> toList(int index) {
        int top = lua_gettop(L);
        if (lua_istable(L, index) == 1) {
            lua_getglobal(L, "unpack");
            lua_insert(L, -2);
            try {
                if (lua_pcall(L, 1, Consts.LUA_MULTRET, 0) == 0) {
                    int len = lua_gettop(L);
                    ArrayList<Object> list = new ArrayList<>();
                    list.ensureCapacity(len - top + 1);
                    for (int i = top; i <= len; ++i) {
                        list.add(toObject(i));
                    }
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

    public Map<Object, Object> toMap(int index) {
        int top = lua_gettop(L);
        if (lua_istable(L, index) == 1) {
            lua_pushnil(L);
            Map<Object, Object> map = new HashMap<>();
            while(lua_next(L, -2) != 0) {
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
     * See {@link #lua_gettop}
     */
    public int gettop() {
        return lua_gettop(L);
    }

    /**
     * See {@link #lua_settop}
     */
    public void settop(int top) {
        lua_settop(L, top);
    }

    /**
     * See {@link #luaL_loadstring}
     */
    public int load(String collect) {
        return luaL_loadstring(L, collect);
    }

    /**
     * See {@link #luaL_loadbuffer}
     */
    public int load(Buffer buffer, String name) {
        if (buffer.isDirect()) {
            return luaL_loadbuffer(L, buffer, buffer.limit(), name);
        }
        return -1;
    }

    /**
     * See {@link #lua_pcall}
     */
    public int pcall(int nargs, int nresults) {
        return lua_pcall(L, nargs, nresults, 0);
    }

    /**
     * See {@link #lua_resume}
     */
    public int resume(int nargs) {
        return lua_resume(L, nargs);
    }

    /**
     * See {@link #lua_newthread}
     */
    public Jua newthread() {
        return new Jua(lua_newthread(L), this);
    }

    public void openBitLibrary() {
        luaopen_bit(L);
    }

    public void openDebugLibrary() {
        luaopen_debug(L);
    }

    public void openIOLibrary() {
        luaopen_io(L);
    }

    public void openMathLibrary() {
        luaopen_math(L);
    }

    public void openOsLibrary() {
        luaopen_os(L);
    }

    public void openPackageLibrary() {
        luaopen_package(L);
    }

    public void openStringLibrary() {
        luaopen_string(L);
    }

    public void openTableLibrary() {
        luaopen_table(L);
    }

    public int ref() {
        return luaL_ref(L, Consts.LUA_REGISTRYINDEX);
    }

    public void unref(int ref) {
        luaL_unref(L, Consts.LUA_REGISTRYINDEX, ref);
    }

    public void refget(int ref) {
        lua_rawgeti(L, Consts.LUA_REGISTRYINDEX, ref);
    }

    public void getglobal(String name) {
        lua_getglobal(L, name);
    }

    public void setglobal(String name) {
        lua_setglobal(L, name);
    }

    public void newtable() {
        lua_newtable(L);
    }

    public void gettable(int index) {
        lua_gettable(L, index);
    }

    public void settable(int index) {
        lua_settable(L, index);
    }

    public void getfield(int index, String k) {
        lua_getfield(L, index, k);
    }

    public void setfield(int index, String k) {
        lua_setfield(L, index, k);
    }

    public void rawget(int index) {
        lua_rawget(L, index);
    }

    public void rawset(int index) {
        lua_rawset(L, index);
    }

    public void rawgeti(int index, int n) {
        lua_rawgeti(L, index, n);
    }

    public void rawseti(int index, int n) {
        lua_rawseti(L, index, n);
    }

    /**
     * See {@link #lua_type}
     */
    public int type(int index) {
        return lua_type(L, index);
    }

    public boolean isnil(int index) {
        return lua_isnil(L, index) != 0;
    }

    public boolean isboolean(int index) {
        return lua_isboolean(L, index) != 0;
    }

    public boolean istable(int index) {
        return lua_istable(L, index) != 0;
    }

    public boolean isstring(int index) {
        return lua_isstring(L, index) != 0;
    }

    public boolean isnumber(int index) {
        return lua_isnumber(L, index) != 0;
    }

    public boolean isnone(int index) {
        return lua_isnone(L, index) != 0;
    }

    public int next(int i) {
        return lua_next(L, i);
    }

    public void pop(int i) {
        lua_pop(L, i);
    }

    public void pushnil() {
        lua_pushnil(L);
    }

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

    @Override
    public void close() throws Exception {
        if (mainThread == this) {
            dispose();
        }
    }
}
