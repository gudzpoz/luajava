package party.iroiro.luajava.value;

import org.jetbrains.annotations.NotNull;
import party.iroiro.luajava.Lua;

import java.util.*;

public class LuaTableValue extends AbstractRefLuaValue implements LuaValue {
    public LuaTableValue(Lua L, Lua.LuaType type) {
        super(L, type);
    }

    @Override
    public int length() {
        Lua L = state();
        push(L);
        int length = L.rawLength(-1);
        L.pop(1);
        return length;
    }

    @NotNull
    @Override
    public Set<Entry<LuaValue, LuaValue>> entrySet() {
        return new AbstractLuaTableSet();
    }

    @Override
    public boolean containsKey(Object key) {
        Lua L = state();
        push(L);
        L.push(key, Lua.Conversion.SEMI);
        L.getTable(-2);
        boolean containsKey = !L.isNil(-1);
        L.pop(2);
        return containsKey;
    }

    private LuaValue putRaw(Object key, Object value) {
        Lua L = state();
        push(L);
        L.push(key, Lua.Conversion.SEMI);
        L.pushValue(-1);
        L.getTable(-3);
        LuaValue old = L.get();
        L.push(value, Lua.Conversion.SEMI);
        L.setTable(-3);
        L.pop(1);
        return old;
    }

    @Override
    public LuaValue remove(Object key) {
        return putRaw(key, null);
    }

    @Override
    public LuaValue put(LuaValue key, LuaValue value) {
        return putRaw(key, value);
    }

    @NotNull
    @Override
    public LuaValue set(Object key, Object value) {
        return putRaw(key, value);
    }

    @Override
    public LuaValue set(int key, Object value) {
        return putRaw(key, value);
    }

    @Override
    public LuaValue get(int i) {
        Lua L = state();
        push(L);
        L.push(i);
        L.getTable(-2);
        LuaValue luaValue = L.get();
        L.pop(1);
        return luaValue;
    }

    @Override
    public LuaValue get(String key) {
        Lua L = state();
        push(L);
        L.getField(-1, key);
        LuaValue value = L.get();
        L.pop(1);
        return value;
    }

    @Override
    public LuaValue get(LuaValue i) {
        Lua L = state();
        push(L);
        i.push(L);
        L.getTable(-2);
        LuaValue luaValue = L.get();
        L.pop(1);
        return luaValue;
    }

    @Override
    public LuaValue get(Object key) {
        Lua L = state();
        push(L);
        L.push(key, Lua.Conversion.SEMI);
        L.getTable(-2);
        LuaValue value = L.get();
        L.pop(1);
        return value;
    }

    protected class AbstractLuaTableSet extends AbstractSet<Entry<LuaValue, LuaValue>> {

        @NotNull
        @Override
        public Iterator<Entry<LuaValue, LuaValue>> iterator() {
            Lua L = state();
            return new Iterator<Entry<LuaValue, LuaValue>>() {
                LuaValue keyRef = L.fromNull();

                @Override
                public boolean hasNext() {
                    push(L);
                    keyRef.push(L);
                    boolean ended = L.next(-2) == 0;
                    L.pop(ended ? 1 : 3);
                    return !ended;
                }

                @Override
                public Entry<LuaValue, LuaValue> next() {
                    push(L);
                    keyRef.push(L);
                    boolean ended = L.next(-2) == 0;
                    if (ended) {
                        L.pop(1);
                        throw new NoSuchElementException();
                    }
                    LuaValue value = L.get();
                    keyRef = L.get();
                    L.pop(1);
                    return new SimpleEntry<>(keyRef, value);
                }

                @Override
                public void remove() {
                    if (keyRef.type().equals(Lua.LuaType.NIL)) {
                        throw new IllegalStateException();
                    }
                    push(L);
                    keyRef.push(L);
                    L.pushNil();
                    L.setTable(-3);
                    L.pop(1);
                }
            };
        }

        @Override
        public int size() {
            int n = 0;
            Lua L = state();
            push(L);
            L.pushNil();
            while (L.next(-2) != 0) {
                n++;
                L.pop(1);
            }
            L.pop(1);
            return n;
        }
    }
}
