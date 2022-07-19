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
