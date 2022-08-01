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

package party.iroiro.luajava;

/**
 * Generated with <code>generate-consts.awk</code>:
 * <pre><code>awk -f jni/scripts/generate-consts.awk \
 *     jni/luajit/src/lua.h \
 *     jni/luajit/src/lauxlib.h \
 *     &gt; src/main/java/party/iroiro/jua/Consts.java</code></pre>
 */
public abstract class Consts {
    /**
     * Generated from jni/luajit/src/lua.h (line 30):
     * <code>#define LUA_MULTRET	(-1)</code>
     */
    public static final int LUA_MULTRET = (-1);

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
