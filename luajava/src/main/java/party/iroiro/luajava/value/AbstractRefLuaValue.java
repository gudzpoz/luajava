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

package party.iroiro.luajava.value;

import org.jetbrains.annotations.Nullable;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.cleaner.LuaReferable;

public abstract class AbstractRefLuaValue extends AbstractLuaValue<Lua> implements LuaReferable {
    private final int ref;

    public AbstractRefLuaValue(Lua L, Lua.LuaType type) {
        super(L, type);
        this.ref = L.ref();
    }

    @Override
    public void push(Lua L) throws LuaException {
        if (L.getMainState() != this.L.getMainState()) {
            throw new LuaException(
                    LuaException.LuaError.MEMORY,
                    "Unable to pass Lua values between different Lua states"
            );
        }
        L.refGet(ref);
    }

    @Override
    public @Nullable Object toJavaObject() {
        push(L);
        Object o = L.toObject(-1);
        L.pop(1);
        return o;
    }

    @Override
    public int getReference() {
        return ref;
    }

    public @Nullable LuaValue[] call(Object... parameters) {
        int top = L.getTop();
        push(L);
        for (Object o : parameters) {
            L.push(o, Lua.Conversion.SEMI);
        }
        L.pCall(parameters.length, Consts.LUA_MULTRET);
        int returnCount = L.getTop() - top;
        LuaValue[] values = new LuaValue[returnCount];
        for (int i = 0; i < returnCount; i++) {
            values[returnCount - i - 1] = L.get();
        }
        return values;
    }
}
