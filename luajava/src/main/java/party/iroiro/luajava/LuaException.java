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
 * A wrapper around a Lua error message
 */
public class LuaException extends RuntimeException {
    public final LuaError type;

    public LuaException(LuaError type, String message) {
        super(message);
        this.type = type;
    }

    @Override
    public String toString() {
        return type + ": " + super.toString();
    }

    /**
     * Lua-relevant error types.
     *
     * <p>
     * Integer values of Lua error codes may vary between Lua versions.
     * This library handles the conversion from the Lua integers to interpretable Java enum values.
     * </p>
     */
    public enum LuaError {
        /**
         * a file-related error
         */
        FILE,
        /**
         * error while running a __gc metamethod
         */
        GC,
        /**
         * error while running the message handler
         */
        HANDLER,
        /**
         * memory allocation error
         */
        MEMORY,
        /**
         * no errors
         */
        OK,
        /**
         * a runtime error
         */
        RUNTIME,
        /**
         * syntax error during precompilation
         */
        SYNTAX,

        /**
         * the thread (coroutine) yields
         */
        YIELD,

        /**
         * unknown error code
         */
        UNKNOWN,

        /**
         * a Java-side error
         */
        JAVA,
    }
}
