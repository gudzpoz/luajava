package party.iroiro.luajava.value;

import org.jetbrains.annotations.Nullable;
import party.iroiro.luajava.Lua;

import java.util.Objects;

public abstract class ImmutableLuaValue<T> extends AbstractLuaValue {
    protected final T value;

    protected ImmutableLuaValue(Lua L, Lua.LuaType type, T value) {
        super(L, type);
        this.value = value;
    }

    @Override
    public void close() {
        // nothing
    }

    @Override
    public @Nullable Object toJavaObject() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && o instanceof ImmutableLuaValue
                && Objects.equals(value, ((ImmutableLuaValue<?>) o).value);
    }

    public static LuaValue NIL(Lua L) {
        return new ImmutableLuaValue<Void>(L, Lua.LuaType.NIL, null) {
            @Override
            public void push() {
                L.pushNil();
            }
        };
    }

    private static class ImmutableBoolean extends ImmutableLuaValue<Boolean> {
        private ImmutableBoolean(Lua L, Boolean value) {
            super(L, Lua.LuaType.BOOLEAN, value);
        }

        @Override
        public void push() {
            L.push(value);
        }
    }

    public static LuaValue TRUE(Lua L) {
        return new ImmutableBoolean(L, true);
    }

    public static LuaValue FALSE(Lua L) {
        return new ImmutableBoolean(L, false);
    }

    private static class ImmutableNumber extends ImmutableLuaValue<Double> {
        private ImmutableNumber(Lua L, Double value) {
            super(L, Lua.LuaType.NUMBER, value);
        }

        @Override
        public void push() {
            L.push(value);
        }
    }

    private static class ImmutableString extends ImmutableLuaValue<String> {
        private ImmutableString(Lua L, String value) {
            super(L, Lua.LuaType.STRING, value);
        }

        @Override
        public void push() {
            L.push(value);
        }
    }

    public static LuaValue NUMBER(Lua L, double n) {
        return new ImmutableNumber(L, n);
    }

    public static LuaValue STRING(Lua L, String s) {
        return new ImmutableString(L, s);
    }
}
