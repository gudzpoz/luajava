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
 * Functional alternative to C closures in Lua to allow, for example, lambda grammar
 */
public interface JFunction {
    /**
     * The function body
     *
     * <p>
     * The call follows the protocol of {@code lua_CFunction} with a bit of modification:
     * </p>
     * <p>
     * In order to communicate properly with Lua, a C function must use the following protocol,
     * which defines the way parameters and results are passed: a C function receives its arguments
     * from Lua in its stack in direct order (the first argument is pushed first).
     * So, when the function starts, lua_gettop(L) returns the number of arguments received
     * by the function. The first argument (if any) is at index 1 and its last argument is at index
     * lua_gettop(L). To return values to Lua, a C function just pushes them onto the stack,
     * in direct order (the first result is pushed first), and returns the number of results.
     * Any other value in the stack below the results will be properly discarded by Lua.
     * Like a Lua function, a C function called by Lua can also return many results.
     * </p>
     * <p>
     * To yield a Lua error, push an error message onto the top of the stack and return -1.
     * </p>
     *
     * @param L the current lua state
     * @return the number of return values pushed onto the stack (-1 to indicate an error)
     */
    int __call(Lua L);
}
