/*
 * Copyright (C) 2022 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package party.iroiro.luajava.luajit;

/**
 * Generated with <code>generate-consts.awk</code>:
 * <pre><code>awk -f scripts/generate-consts.awk \
 *     .../lua.h \
 *     .../lauxlib.h \
 *     &gt; .../party/iroiro/jua/...Consts.java</code></pre>
 */
public abstract class LuaJitConsts {
    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 19):
     * <pre><code>#define LUA_VERSION	"Lua 5.1"</code></pre>
     */
    public static final String LUA_VERSION = "Lua 5.1";

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 20):
     * <pre><code>#define LUA_RELEASE	"Lua 5.1.4"</code></pre>
     */
    public static final String LUA_RELEASE = "Lua 5.1.4";

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 21):
     * <pre><code>#define LUA_VERSION_NUM	501</code></pre>
     */
    public static final int LUA_VERSION_NUM = 501;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 22):
     * <pre><code>#define LUA_COPYRIGHT	"Copyright (C) 1994-2008 Lua.org, PUC-Rio"</code></pre>
     */
    public static final String LUA_COPYRIGHT = "Copyright (C) 1994-2008 Lua.org, PUC-Rio";

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 23):
     * <pre><code>#define LUA_AUTHORS	"R. Ierusalimschy, L. H. de Figueiredo &amp; W. Celes"</code></pre>
     */
    public static final String LUA_AUTHORS = "R. Ierusalimschy, L. H. de Figueiredo & W. Celes";

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 27):
     * <pre><code>#define	LUA_SIGNATURE	"\033Lua"</code></pre>
     */
    public static final String LUA_SIGNATURE = "\033Lua";

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 30):
     * <pre><code>#define LUA_MULTRET	(-1)</code></pre>
     */
    public static final int LUA_MULTRET = (-1);

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 36):
     * <pre><code>#define LUA_REGISTRYINDEX	(-10000)</code></pre>
     */
    public static final int LUA_REGISTRYINDEX = (-10000);

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 37):
     * <pre><code>#define LUA_ENVIRONINDEX	(-10001)</code></pre>
     */
    public static final int LUA_ENVIRONINDEX = (-10001);

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 38):
     * <pre><code>#define LUA_GLOBALSINDEX	(-10002)</code></pre>
     */
    public static final int LUA_GLOBALSINDEX = (-10002);

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 43):
     * <pre><code>#define LUA_OK		0</code></pre>
     */
    public static final int LUA_OK = 0;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 44):
     * <pre><code>#define LUA_YIELD	1</code></pre>
     */
    public static final int LUA_YIELD = 1;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 45):
     * <pre><code>#define LUA_ERRRUN	2</code></pre>
     */
    public static final int LUA_ERRRUN = 2;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 46):
     * <pre><code>#define LUA_ERRSYNTAX	3</code></pre>
     */
    public static final int LUA_ERRSYNTAX = 3;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 47):
     * <pre><code>#define LUA_ERRMEM	4</code></pre>
     */
    public static final int LUA_ERRMEM = 4;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 48):
     * <pre><code>#define LUA_ERRERR	5</code></pre>
     */
    public static final int LUA_ERRERR = 5;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 73):
     * <pre><code>#define LUA_TNONE		(-1)</code></pre>
     */
    public static final int LUA_TNONE = (-1);

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 75):
     * <pre><code>#define LUA_TNIL		0</code></pre>
     */
    public static final int LUA_TNIL = 0;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 76):
     * <pre><code>#define LUA_TBOOLEAN		1</code></pre>
     */
    public static final int LUA_TBOOLEAN = 1;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 77):
     * <pre><code>#define LUA_TLIGHTUSERDATA	2</code></pre>
     */
    public static final int LUA_TLIGHTUSERDATA = 2;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 78):
     * <pre><code>#define LUA_TNUMBER		3</code></pre>
     */
    public static final int LUA_TNUMBER = 3;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 79):
     * <pre><code>#define LUA_TSTRING		4</code></pre>
     */
    public static final int LUA_TSTRING = 4;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 80):
     * <pre><code>#define LUA_TTABLE		5</code></pre>
     */
    public static final int LUA_TTABLE = 5;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 81):
     * <pre><code>#define LUA_TFUNCTION		6</code></pre>
     */
    public static final int LUA_TFUNCTION = 6;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 82):
     * <pre><code>#define LUA_TUSERDATA		7</code></pre>
     */
    public static final int LUA_TUSERDATA = 7;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 83):
     * <pre><code>#define LUA_TTHREAD		8</code></pre>
     */
    public static final int LUA_TTHREAD = 8;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 88):
     * <pre><code>#define LUA_MINSTACK	20</code></pre>
     */
    public static final int LUA_MINSTACK = 20;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 222):
     * <pre><code>#define LUA_GCSTOP		0</code></pre>
     */
    public static final int LUA_GCSTOP = 0;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 223):
     * <pre><code>#define LUA_GCRESTART		1</code></pre>
     */
    public static final int LUA_GCRESTART = 1;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 224):
     * <pre><code>#define LUA_GCCOLLECT		2</code></pre>
     */
    public static final int LUA_GCCOLLECT = 2;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 225):
     * <pre><code>#define LUA_GCCOUNT		3</code></pre>
     */
    public static final int LUA_GCCOUNT = 3;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 226):
     * <pre><code>#define LUA_GCCOUNTB		4</code></pre>
     */
    public static final int LUA_GCCOUNTB = 4;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 227):
     * <pre><code>#define LUA_GCSTEP		5</code></pre>
     */
    public static final int LUA_GCSTEP = 5;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 228):
     * <pre><code>#define LUA_GCSETPAUSE		6</code></pre>
     */
    public static final int LUA_GCSETPAUSE = 6;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 229):
     * <pre><code>#define LUA_GCSETSTEPMUL	7</code></pre>
     */
    public static final int LUA_GCSETSTEPMUL = 7;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 230):
     * <pre><code>#define LUA_GCISRUNNING		9</code></pre>
     */
    public static final int LUA_GCISRUNNING = 9;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 313):
     * <pre><code>#define LUA_HOOKCALL	0</code></pre>
     */
    public static final int LUA_HOOKCALL = 0;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 314):
     * <pre><code>#define LUA_HOOKRET	1</code></pre>
     */
    public static final int LUA_HOOKRET = 1;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 315):
     * <pre><code>#define LUA_HOOKLINE	2</code></pre>
     */
    public static final int LUA_HOOKLINE = 2;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 316):
     * <pre><code>#define LUA_HOOKCOUNT	3</code></pre>
     */
    public static final int LUA_HOOKCOUNT = 3;

    /**
     * Generated from luajit/jni/luajit/src/lua.h (line 317):
     * <pre><code>#define LUA_HOOKTAILRET 4</code></pre>
     */
    public static final int LUA_HOOKTAILRET = 4;

    /**
     * Generated from luajit/jni/luajit/src/lauxlib.h (line 461):
     * <pre><code>#define LUA_NOREF       (-2)</code></pre>
     */
    public static final int LUA_NOREF = (-2);

    /**
     * Generated from luajit/jni/luajit/src/lauxlib.h (line 462):
     * <pre><code>#define LUA_REFNIL      (-1)</code></pre>
     */
    public static final int LUA_REFNIL = (-1);

}
