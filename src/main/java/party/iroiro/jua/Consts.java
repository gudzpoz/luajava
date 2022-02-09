package party.iroiro.jua;

/**
 * Generated with <code>generate-consts.awk</code>:
 * <pre><code>awk -f jni/scripts/generate-consts.awk \
 *     jni/luajit/src/lua.h \
 *     jni/luajit/src/lauxlib.h \
 *     &gt; src/main/java/party/iroiro/jua/Consts.java</code></pre>
 */
public class Consts {
    /**
     * Generated from jni/luajit/src/lua.h (line 19):
     * <code>#define LUA_VERSION	"Lua 5.1"</code>
     */
    public static final String LUA_VERSION = "Lua 5.1";

    /**
     * Generated from jni/luajit/src/lua.h (line 20):
     * <code>#define LUA_RELEASE	"Lua 5.1.4"</code>
     */
    public static final String LUA_RELEASE = "Lua 5.1.4";

    /**
     * Generated from jni/luajit/src/lua.h (line 21):
     * <code>#define LUA_VERSION_NUM	501</code>
     */
    public static final int LUA_VERSION_NUM = 501;

    /**
     * Generated from jni/luajit/src/lua.h (line 22):
     * <code>#define LUA_COPYRIGHT	"Copyright (C) 1994-2008 Lua.org, PUC-Rio"</code>
     */
    public static final String LUA_COPYRIGHT = "Copyright (C) 1994-2008 Lua.org, PUC-Rio";

    /**
     * Generated from jni/luajit/src/lua.h (line 23):
     * <code>#define LUA_AUTHORS	"R. Ierusalimschy, L. H. de Figueiredo &amp; W. Celes"</code>
     */
    public static final String LUA_AUTHORS = "R. Ierusalimschy, L. H. de Figueiredo & W. Celes";

    /**
     * Generated from jni/luajit/src/lua.h (line 27):
     * <code>#define	LUA_SIGNATURE	"\033Lua"</code>
     */
    public static final String LUA_SIGNATURE = "\033Lua";

    /**
     * Generated from jni/luajit/src/lua.h (line 30):
     * <code>#define LUA_MULTRET	(-1)</code>
     */
    public static final int LUA_MULTRET = (-1);

    /**
     * Generated from jni/luajit/src/lua.h (line 36):
     * <code>#define LUA_REGISTRYINDEX	(-10000)</code>
     */
    public static final int LUA_REGISTRYINDEX = (-10000);

    /**
     * Generated from jni/luajit/src/lua.h (line 37):
     * <code>#define LUA_ENVIRONINDEX	(-10001)</code>
     */
    public static final int LUA_ENVIRONINDEX = (-10001);

    /**
     * Generated from jni/luajit/src/lua.h (line 38):
     * <code>#define LUA_GLOBALSINDEX	(-10002)</code>
     */
    public static final int LUA_GLOBALSINDEX = (-10002);

    /**
     * Generated from jni/luajit/src/lua.h (line 43):
     * <code>#define LUA_OK		0</code>
     */
    public static final int LUA_OK = 0;

    /**
     * Generated from jni/luajit/src/lua.h (line 44):
     * <code>#define LUA_YIELD	1</code>
     */
    public static final int LUA_YIELD = 1;

    /**
     * Generated from jni/luajit/src/lua.h (line 45):
     * <code>#define LUA_ERRRUN	2</code>
     */
    public static final int LUA_ERRRUN = 2;

    /**
     * Generated from jni/luajit/src/lua.h (line 46):
     * <code>#define LUA_ERRSYNTAX	3</code>
     */
    public static final int LUA_ERRSYNTAX = 3;

    /**
     * Generated from jni/luajit/src/lua.h (line 47):
     * <code>#define LUA_ERRMEM	4</code>
     */
    public static final int LUA_ERRMEM = 4;

    /**
     * Generated from jni/luajit/src/lua.h (line 48):
     * <code>#define LUA_ERRERR	5</code>
     */
    public static final int LUA_ERRERR = 5;

    /**
     * Generated from jni/luajit/src/lua.h (line 73):
     * <code>#define LUA_TNONE		(-1)</code>
     */
    public static final int LUA_TNONE = (-1);

    /**
     * Generated from jni/luajit/src/lua.h (line 75):
     * <code>#define LUA_TNIL		0</code>
     */
    public static final int LUA_TNIL = 0;

    /**
     * Generated from jni/luajit/src/lua.h (line 76):
     * <code>#define LUA_TBOOLEAN		1</code>
     */
    public static final int LUA_TBOOLEAN = 1;

    /**
     * Generated from jni/luajit/src/lua.h (line 77):
     * <code>#define LUA_TLIGHTUSERDATA	2</code>
     */
    public static final int LUA_TLIGHTUSERDATA = 2;

    /**
     * Generated from jni/luajit/src/lua.h (line 78):
     * <code>#define LUA_TNUMBER		3</code>
     */
    public static final int LUA_TNUMBER = 3;

    /**
     * Generated from jni/luajit/src/lua.h (line 79):
     * <code>#define LUA_TSTRING		4</code>
     */
    public static final int LUA_TSTRING = 4;

    /**
     * Generated from jni/luajit/src/lua.h (line 80):
     * <code>#define LUA_TTABLE		5</code>
     */
    public static final int LUA_TTABLE = 5;

    /**
     * Generated from jni/luajit/src/lua.h (line 81):
     * <code>#define LUA_TFUNCTION		6</code>
     */
    public static final int LUA_TFUNCTION = 6;

    /**
     * Generated from jni/luajit/src/lua.h (line 82):
     * <code>#define LUA_TUSERDATA		7</code>
     */
    public static final int LUA_TUSERDATA = 7;

    /**
     * Generated from jni/luajit/src/lua.h (line 83):
     * <code>#define LUA_TTHREAD		8</code>
     */
    public static final int LUA_TTHREAD = 8;

    /**
     * Generated from jni/luajit/src/lua.h (line 88):
     * <code>#define LUA_MINSTACK	20</code>
     */
    public static final int LUA_MINSTACK = 20;

    /**
     * Generated from jni/luajit/src/lua.h (line 222):
     * <code>#define LUA_GCSTOP		0</code>
     */
    public static final int LUA_GCSTOP = 0;

    /**
     * Generated from jni/luajit/src/lua.h (line 223):
     * <code>#define LUA_GCRESTART		1</code>
     */
    public static final int LUA_GCRESTART = 1;

    /**
     * Generated from jni/luajit/src/lua.h (line 224):
     * <code>#define LUA_GCCOLLECT		2</code>
     */
    public static final int LUA_GCCOLLECT = 2;

    /**
     * Generated from jni/luajit/src/lua.h (line 225):
     * <code>#define LUA_GCCOUNT		3</code>
     */
    public static final int LUA_GCCOUNT = 3;

    /**
     * Generated from jni/luajit/src/lua.h (line 226):
     * <code>#define LUA_GCCOUNTB		4</code>
     */
    public static final int LUA_GCCOUNTB = 4;

    /**
     * Generated from jni/luajit/src/lua.h (line 227):
     * <code>#define LUA_GCSTEP		5</code>
     */
    public static final int LUA_GCSTEP = 5;

    /**
     * Generated from jni/luajit/src/lua.h (line 228):
     * <code>#define LUA_GCSETPAUSE		6</code>
     */
    public static final int LUA_GCSETPAUSE = 6;

    /**
     * Generated from jni/luajit/src/lua.h (line 229):
     * <code>#define LUA_GCSETSTEPMUL	7</code>
     */
    public static final int LUA_GCSETSTEPMUL = 7;

    /**
     * Generated from jni/luajit/src/lua.h (line 230):
     * <code>#define LUA_GCISRUNNING		9</code>
     */
    public static final int LUA_GCISRUNNING = 9;

    /**
     * Generated from jni/luajit/src/lua.h (line 313):
     * <code>#define LUA_HOOKCALL	0</code>
     */
    public static final int LUA_HOOKCALL = 0;

    /**
     * Generated from jni/luajit/src/lua.h (line 314):
     * <code>#define LUA_HOOKRET	1</code>
     */
    public static final int LUA_HOOKRET = 1;

    /**
     * Generated from jni/luajit/src/lua.h (line 315):
     * <code>#define LUA_HOOKLINE	2</code>
     */
    public static final int LUA_HOOKLINE = 2;

    /**
     * Generated from jni/luajit/src/lua.h (line 316):
     * <code>#define LUA_HOOKCOUNT	3</code>
     */
    public static final int LUA_HOOKCOUNT = 3;

    /**
     * Generated from jni/luajit/src/lua.h (line 317):
     * <code>#define LUA_HOOKTAILRET 4</code>
     */
    public static final int LUA_HOOKTAILRET = 4;

    /**
     * Generated from jni/luajit/src/lauxlib.h (line 461):
     * <code>#define LUA_NOREF       (-2)</code>
     */
    public static final int LUA_NOREF = (-2);

    /**
     * Generated from jni/luajit/src/lauxlib.h (line 462):
     * <code>#define LUA_REFNIL      (-1)</code>
     */
    public static final int LUA_REFNIL = (-1);

}
