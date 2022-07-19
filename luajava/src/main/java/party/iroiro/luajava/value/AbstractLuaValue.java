package party.iroiro.luajava.value;

import org.jetbrains.annotations.Nullable;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;

abstract class AbstractLuaValue implements LuaValue {
    protected final Lua L;
    protected final Lua.LuaType type;

    protected AbstractLuaValue(Lua L, Lua.LuaType type) {
        this.L = L;
        this.type = type;
    }

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
            AbstractLuaValue value = (AbstractLuaValue) o;
            return state().getMainState() == value.state().getMainState() && type == value.type();
        }
        return false;
    }

    public LuaValue get(String key) {
        push();
        L.getField(-1, key);
        LuaValue value = L.get();
        L.pop(1);
        return value;
    }

    public LuaValue get(int i) {
        push();
        L.push(i);
        L.getTable(-2);
        LuaValue luaValue = L.get();
        L.pop(1);
        return luaValue;
    }

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

    public @Nullable LuaValue[] call(Object... parameters) {
        int top = L.getTop();
        push();
        for (Object o : parameters) {
            L.push(o, Lua.Conversion.SEMI);
        }
        if (L.pCall(parameters.length, Consts.LUA_MULTRET) == Lua.LuaError.OK) {
            int returnCount = L.getTop() - top;
            LuaValue[] values = new LuaValue[returnCount];
            for (int i = 0; i < returnCount; i++) {
                values[returnCount - i - 1] = L.get();
            }
            return values;
        } else {
            return null;
        }
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
