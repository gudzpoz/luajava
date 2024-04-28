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

/**
 * Basic implementation of a {@link LuaValue} on some Lua thread
 *
 * @param <T> the Lua thread type
 */
public abstract class AbstractLuaValue<T extends Lua> implements LuaValue {
    protected final T L;
    protected final Lua.LuaType type;

    protected AbstractLuaValue(T L, Lua.LuaType type) {
        this.L = L;
        this.type = type;
    }

    /**
     * Checks whether the other value is accessible from the current Lua thread
     *
     * @param value the other value
     * @throws IllegalArgumentException if inaccessible
     */
    protected void check(LuaValue value) throws IllegalArgumentException {
        if (value.state() != state()) {
            throw new IllegalArgumentException("Expecting values on the same state");
        }
    }

    @Override
    public Lua.LuaType type() {
        return type;
    }

    @Override
    public Lua state() {
        return L;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractLuaValue) {
            AbstractLuaValue<?> value = (AbstractLuaValue<?>) o;
            Lua J = state();
            if (J.getMainState() != value.state().getMainState() || type != value.type()) {
                return false;
            }
            int top = J.getTop();
            push(J);
            value.push(J);
            boolean equal = J.equal(-2, -1);
            J.setTop(top);
            return equal;
        }
        return false;
    }

    @Override
    public LuaValue get(String key) {
        push();
        L.getField(-1, key);
        LuaValue value = L.get();
        L.pop(1);
        return value;
    }

    @Override
    public LuaValue get(int i) {
        push();
        L.push(i);
        L.getTable(-2);
        LuaValue luaValue = L.get();
        L.pop(1);
        return luaValue;
    }

    @Override
    public LuaValue get(LuaValue i) {
        check(i);

        push();
        i.push();
        L.getTable(-2);
        LuaValue luaValue = L.get();
        L.pop(1);
        return luaValue;
    }

    @Override
    public void set(int i, LuaValue value) {
        check(value);

        push();
        L.push(i);
        value.push();
        L.setTable(-3);
        L.pop(1);
    }

    @Override
    public void set(String key, LuaValue value) {
        check(value);

        push();
        value.push();
        L.setField(-2, key);
        L.pop(1);
    }

    @Override
    public void set(LuaValue key, LuaValue value) {
        check(key);
        check(value);

        push();
        key.push();
        value.push();
        L.setTable(-3);
        L.pop(1);
    }

    public LuaValue[] call(Object... parameters) {
        int top = L.getTop();
        push();
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

    @Override
    public void push(Lua K) {
        if (K == L) {
            push();
        } else if (K.getMainState() == L.getMainState()) {
            push();
            L.xMove(K, 1);
        } else {
            throw new UnsupportedOperationException("Unable to move values between threads that do not share a main state");
        }
    }
}
