package party.iroiro.luajava;

/**
 * Generated with <code>generate-consts.awk</code>:
 * <pre><code>awk -f scripts/generate-consts.awk \
 *     .../lua.h \
 *     .../lauxlib.h \
 *     &gt; .../party/iroiro/jua/...Consts.java</code></pre>
 */
public abstract class Lua52Consts {
    /**
     * Generated from lua52/jni/lua52/lua.h (line 19):
     * <pre><code>#define LUA_VERSION_MAJOR	"5"</code></pre>
     */
    public static final String LUA_VERSION_MAJOR = "5";

    /**
     * Generated from lua52/jni/lua52/lua.h (line 20):
     * <pre><code>#define LUA_VERSION_MINOR	"2"</code></pre>
     */
    public static final String LUA_VERSION_MINOR = "2";

    /**
     * Generated from lua52/jni/lua52/lua.h (line 21):
     * <pre><code>#define LUA_VERSION_NUM		502</code></pre>
     */
    public static final int LUA_VERSION_NUM = 502;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 22):
     * <pre><code>#define LUA_VERSION_RELEASE	"4"</code></pre>
     */
    public static final String LUA_VERSION_RELEASE = "4";

    /**
     * Generated from lua52/jni/lua52/lua.h (line 27):
     * <pre><code>#define LUA_AUTHORS	"R. Ierusalimschy, L. H. de Figueiredo, W. Celes"</code></pre>
     */
    public static final String LUA_AUTHORS = "R. Ierusalimschy, L. H. de Figueiredo, W. Celes";

    /**
     * Generated from lua52/jni/lua52/lua.h (line 31):
     * <pre><code>#define LUA_SIGNATURE	"\033Lua"</code></pre>
     */
    public static final String LUA_SIGNATURE = "\033Lua";

    /**
     * Generated from lua52/jni/lua52/lua.h (line 34):
     * <pre><code>#define LUA_MULTRET	(-1)</code></pre>
     */
    public static final int LUA_MULTRET = (-1);

    /**
     * Generated from lua52/jni/lua52/lua.h (line 45):
     * <pre><code>#define LUA_OK		0</code></pre>
     */
    public static final int LUA_OK = 0;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 46):
     * <pre><code>#define LUA_YIELD	1</code></pre>
     */
    public static final int LUA_YIELD = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 47):
     * <pre><code>#define LUA_ERRRUN	2</code></pre>
     */
    public static final int LUA_ERRRUN = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 48):
     * <pre><code>#define LUA_ERRSYNTAX	3</code></pre>
     */
    public static final int LUA_ERRSYNTAX = 3;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 49):
     * <pre><code>#define LUA_ERRMEM	4</code></pre>
     */
    public static final int LUA_ERRMEM = 4;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 50):
     * <pre><code>#define LUA_ERRGCMM	5</code></pre>
     */
    public static final int LUA_ERRGCMM = 5;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 51):
     * <pre><code>#define LUA_ERRERR	6</code></pre>
     */
    public static final int LUA_ERRERR = 6;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 76):
     * <pre><code>#define LUA_TNONE		(-1)</code></pre>
     */
    public static final int LUA_TNONE = (-1);

    /**
     * Generated from lua52/jni/lua52/lua.h (line 78):
     * <pre><code>#define LUA_TNIL		0</code></pre>
     */
    public static final int LUA_TNIL = 0;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 79):
     * <pre><code>#define LUA_TBOOLEAN		1</code></pre>
     */
    public static final int LUA_TBOOLEAN = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 80):
     * <pre><code>#define LUA_TLIGHTUSERDATA	2</code></pre>
     */
    public static final int LUA_TLIGHTUSERDATA = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 81):
     * <pre><code>#define LUA_TNUMBER		3</code></pre>
     */
    public static final int LUA_TNUMBER = 3;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 82):
     * <pre><code>#define LUA_TSTRING		4</code></pre>
     */
    public static final int LUA_TSTRING = 4;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 83):
     * <pre><code>#define LUA_TTABLE		5</code></pre>
     */
    public static final int LUA_TTABLE = 5;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 84):
     * <pre><code>#define LUA_TFUNCTION		6</code></pre>
     */
    public static final int LUA_TFUNCTION = 6;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 85):
     * <pre><code>#define LUA_TUSERDATA		7</code></pre>
     */
    public static final int LUA_TUSERDATA = 7;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 86):
     * <pre><code>#define LUA_TTHREAD		8</code></pre>
     */
    public static final int LUA_TTHREAD = 8;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 88):
     * <pre><code>#define LUA_NUMTAGS		9</code></pre>
     */
    public static final int LUA_NUMTAGS = 9;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 93):
     * <pre><code>#define LUA_MINSTACK	20</code></pre>
     */
    public static final int LUA_MINSTACK = 20;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 97):
     * <pre><code>#define LUA_RIDX_MAINTHREAD	1</code></pre>
     */
    public static final int LUA_RIDX_MAINTHREAD = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 98):
     * <pre><code>#define LUA_RIDX_GLOBALS	2</code></pre>
     */
    public static final int LUA_RIDX_GLOBALS = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 185):
     * <pre><code>#define LUA_OPSUB	1</code></pre>
     */
    public static final int LUA_OPSUB = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 186):
     * <pre><code>#define LUA_OPMUL	2</code></pre>
     */
    public static final int LUA_OPMUL = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 187):
     * <pre><code>#define LUA_OPDIV	3</code></pre>
     */
    public static final int LUA_OPDIV = 3;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 188):
     * <pre><code>#define LUA_OPMOD	4</code></pre>
     */
    public static final int LUA_OPMOD = 4;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 189):
     * <pre><code>#define LUA_OPPOW	5</code></pre>
     */
    public static final int LUA_OPPOW = 5;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 190):
     * <pre><code>#define LUA_OPUNM	6</code></pre>
     */
    public static final int LUA_OPUNM = 6;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 194):
     * <pre><code>#define LUA_OPEQ	0</code></pre>
     */
    public static final int LUA_OPEQ = 0;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 195):
     * <pre><code>#define LUA_OPLT	1</code></pre>
     */
    public static final int LUA_OPLT = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 196):
     * <pre><code>#define LUA_OPLE	2</code></pre>
     */
    public static final int LUA_OPLE = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 281):
     * <pre><code>#define LUA_GCSTOP		0</code></pre>
     */
    public static final int LUA_GCSTOP = 0;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 282):
     * <pre><code>#define LUA_GCRESTART		1</code></pre>
     */
    public static final int LUA_GCRESTART = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 283):
     * <pre><code>#define LUA_GCCOLLECT		2</code></pre>
     */
    public static final int LUA_GCCOLLECT = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 284):
     * <pre><code>#define LUA_GCCOUNT		3</code></pre>
     */
    public static final int LUA_GCCOUNT = 3;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 285):
     * <pre><code>#define LUA_GCCOUNTB		4</code></pre>
     */
    public static final int LUA_GCCOUNTB = 4;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 286):
     * <pre><code>#define LUA_GCSTEP		5</code></pre>
     */
    public static final int LUA_GCSTEP = 5;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 287):
     * <pre><code>#define LUA_GCSETPAUSE		6</code></pre>
     */
    public static final int LUA_GCSETPAUSE = 6;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 288):
     * <pre><code>#define LUA_GCSETSTEPMUL	7</code></pre>
     */
    public static final int LUA_GCSETSTEPMUL = 7;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 289):
     * <pre><code>#define LUA_GCSETMAJORINC	8</code></pre>
     */
    public static final int LUA_GCSETMAJORINC = 8;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 290):
     * <pre><code>#define LUA_GCISRUNNING		9</code></pre>
     */
    public static final int LUA_GCISRUNNING = 9;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 291):
     * <pre><code>#define LUA_GCGEN		10</code></pre>
     */
    public static final int LUA_GCGEN = 10;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 292):
     * <pre><code>#define LUA_GCINC		11</code></pre>
     */
    public static final int LUA_GCINC = 11;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 360):
     * <pre><code>#define LUA_HOOKCALL	0</code></pre>
     */
    public static final int LUA_HOOKCALL = 0;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 361):
     * <pre><code>#define LUA_HOOKRET	1</code></pre>
     */
    public static final int LUA_HOOKRET = 1;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 362):
     * <pre><code>#define LUA_HOOKLINE	2</code></pre>
     */
    public static final int LUA_HOOKLINE = 2;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 363):
     * <pre><code>#define LUA_HOOKCOUNT	3</code></pre>
     */
    public static final int LUA_HOOKCOUNT = 3;

    /**
     * Generated from lua52/jni/lua52/lua.h (line 364):
     * <pre><code>#define LUA_HOOKTAILCALL 4</code></pre>
     */
    public static final int LUA_HOOKTAILCALL = 4;

    /**
     * Generated from lua52/jni/lua52/lauxlib.h (line 513):
     * <pre><code>#define LUA_NOREF       (-2)</code></pre>
     */
    public static final int LUA_NOREF = (-2);

    /**
     * Generated from lua52/jni/lua52/lauxlib.h (line 514):
     * <pre><code>#define LUA_REFNIL      (-1)</code></pre>
     */
    public static final int LUA_REFNIL = (-1);

    /**
     * Generated from lua52/jni/lua52/lauxlib.h (line 629):
     * <pre><code>#define LUA_FILEHANDLE          "FILE*"</code></pre>
     */
    public static final String LUA_FILEHANDLE = "FILE*";

}
