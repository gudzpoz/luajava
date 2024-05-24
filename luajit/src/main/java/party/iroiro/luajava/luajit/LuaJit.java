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

import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.LuaException.LuaError;
import party.iroiro.luajava.LuaNatives;

import java.util.concurrent.atomic.AtomicReference;

import static party.iroiro.luajava.luajit.LuaJitConsts.*;

/**
 * A thin wrapper around a LuaJIT Lua state
 */
public class LuaJit extends AbstractLua {
    private final static AtomicReference<LuaJitNatives> natives = new AtomicReference<>();

    /**
     * Creates a new Lua state
     *
     * @throws LinkageError if LuaJIT natives unavailable
     */
    public LuaJit() throws LinkageError {
        super(getNatives());
    }

    protected LuaJit(long L, int id, AbstractLua main) {
        super(main.getLuaNatives(), L, id, main);
    }

    private static LuaNatives getNatives() throws LinkageError {
        synchronized (natives) {
            if (natives.get() == null) {
                try {
                    natives.set(new LuaJitNatives());
                } catch (IllegalStateException e) {
                    throw new LinkageError("Unable to find natives or init", e);
                }
            }
            return natives.get();
        }
    }

    @Override
    protected AbstractLua newThread(long L, int id, AbstractLua mainThread) {
        return new LuaJit(L, id, mainThread);
    }


    @Override
    public LuaError convertError(int code) {
        switch (code) {
            case LUA_OK:
                return LuaError.OK;
            case LUA_YIELD:
                return LuaError.YIELD;
            case LUA_ERRRUN:
                return LuaError.RUNTIME;
            case LUA_ERRSYNTAX:
                return LuaError.SYNTAX;
            case LUA_ERRMEM:
                return LuaError.MEMORY;
            case LUA_ERRERR:
                return LuaError.HANDLER;
            default:
                throw new LuaException(LuaError.RUNTIME, "Unrecognized error code");
        }
    }

    @Override
    public LuaType convertType(int code) {
        switch (code) {
            case LUA_TBOOLEAN:
                return LuaType.BOOLEAN;
            case LUA_TFUNCTION:
                return LuaType.FUNCTION;
            case LUA_TLIGHTUSERDATA:
                return LuaType.LIGHTUSERDATA;
            case LUA_TNIL:
                return LuaType.NIL;
            case LUA_TNONE:
                return LuaType.NONE;
            case LUA_TNUMBER:
                return LuaType.NUMBER;
            case LUA_TSTRING:
                return LuaType.STRING;
            case LUA_TTABLE:
                return LuaType.TABLE;
            case LUA_TTHREAD:
                return LuaType.THREAD;
            case LUA_TUSERDATA:
                return LuaType.USERDATA;
            default:
                throw new LuaException(LuaError.RUNTIME, "Unrecognized type code");
        }
    }
}
