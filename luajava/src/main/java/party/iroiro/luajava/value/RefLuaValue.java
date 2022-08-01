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
import party.iroiro.luajava.Lua;

public class RefLuaValue extends AbstractLuaValue {
    private final int ref;

    public RefLuaValue(Lua L, Lua.LuaType type) {
        super(L, type);
        this.ref = L.ref();
    }

    @Override
    public void push() {
        L.refGet(ref);
    }

    @Override
    public @Nullable Object toJavaObject() {
        push();
        Object o = L.toObject(-1);
        L.pop(1);
        return o;
    }

    @Override
    public void close() {
        L.unref(ref);
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o) && o instanceof RefLuaValue) {
            RefLuaValue o2 = (RefLuaValue) o;
            if (ref == o2.ref) {
                return true;
            }
            push();
            o2.push(L);
            boolean equal = L.equal(-1, -2);
            L.pop(2);
            return equal;
        }
        return false;
    }
}
