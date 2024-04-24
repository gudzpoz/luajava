package party.iroiro.luajava.luaj;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaValue;

/**
 * Generated with <code>generate-consts.awk</code>:
 * <pre><code>awk -f scripts/generate-consts.awk \
 *     .../lua.h \
 *     .../lauxlib.h \
 *     &gt; .../party/iroiro/jua/...Consts.java</code></pre>
 */
public abstract class LuaJConsts {
    public static final String LUA_VERSION = Lua._VERSION;

    public static final String LUA_RELEASE = LUA_VERSION;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 21):
     * <pre><code>#define LUA_VERSION_NUM	501</code></pre>
     */
    public static final int LUA_VERSION_NUM = 501;

    public static final String LUA_COPYRIGHT = "Copyright (C) 2012 luaj.org";

    public static final int LUA_MULTRET = Lua.LUA_MULTRET;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 36):
     * <pre><code>#define LUA_REGISTRYINDEX	(-10000)</code></pre>
     */
    public static final int LUA_REGISTRYINDEX = (-10000);

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 37):
     * <pre><code>#define LUA_ENVIRONINDEX	(-10001)</code></pre>
     */
    public static final int LUA_ENVIRONINDEX = (-10001);

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 38):
     * <pre><code>#define LUA_GLOBALSINDEX	(-10002)</code></pre>
     */
    public static final int LUA_GLOBALSINDEX = (-10002);

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 43):
     * <pre><code>#define LUA_YIELD	1</code></pre>
     */
    public static final int LUA_YIELD = 1;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 44):
     * <pre><code>#define LUA_ERRRUN	2</code></pre>
     */
    public static final int LUA_ERRRUN = 2;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 45):
     * <pre><code>#define LUA_ERRSYNTAX	3</code></pre>
     */
    public static final int LUA_ERRSYNTAX = 3;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 46):
     * <pre><code>#define LUA_ERRMEM	4</code></pre>
     */
    public static final int LUA_ERRMEM = 4;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 47):
     * <pre><code>#define LUA_ERRERR	5</code></pre>
     */
    public static final int LUA_ERRERR = 5;

    public static final int LUA_TNONE = LuaValue.TNONE;

    public static final int LUA_TNIL = LuaValue.TNIL;

    public static final int LUA_TBOOLEAN = LuaValue.TBOOLEAN;

    public static final int LUA_TLIGHTUSERDATA = LuaValue.TLIGHTUSERDATA;

    public static final int LUA_TNUMBER = LuaValue.TNUMBER;

    public static final int LUA_TSTRING = LuaValue.TSTRING;

    public static final int LUA_TTABLE = LuaValue.TTABLE;

    public static final int LUA_TFUNCTION = LuaValue.TFUNCTION;

    public static final int LUA_TUSERDATA = LuaValue.TUSERDATA;

    public static final int LUA_TTHREAD = LuaValue.TTHREAD;

    /**
     * Generated from lua51/jni/lua51/src/lua.h (line 87):
     * <pre><code>#define LUA_MINSTACK	20</code></pre>
     */
    public static final int LUA_MINSTACK = 20;

    /**
     * Generated from lua51/jni/lua51/src/lauxlib.h (line 547):
     * <pre><code>#define LUA_NOREF       (-2)</code></pre>
     */
    public static final int LUA_NOREF = 0;

    /**
     * Generated from lua51/jni/lua51/src/lauxlib.h (line 548):
     * <pre><code>#define LUA_REFNIL      (-1)</code></pre>
     */
    public static final int LUA_REFNIL = (-1);

}
