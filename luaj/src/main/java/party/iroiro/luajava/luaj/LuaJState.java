package party.iroiro.luajava.luaj;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaJState {
    protected final int id;
    protected final int lid;
    protected final Globals globals;
    protected final LuaThread thread;
    protected final List<List<LuaValue>> luaStacks;
    protected final Map<String, LuaValue> registry;

    protected LuaJState(int id, int lid, Globals globals, LuaThread thread) {
        this.id = id;
        this.lid = lid;
        this.globals = globals;
        this.thread = thread;
        luaStacks = new ArrayList<>();
        luaStacks.add(new ArrayList<>());
        registry = new HashMap<>();
    }

    protected List<LuaValue> stack() {
        assert !luaStacks.isEmpty();
        int size = luaStacks.size();
        return luaStacks.get(size - 1);
    }

    public int getTop() {
        return stack().size();
    }

    public void setTop(int top) {
        List<LuaValue> stack = stack();
        if (top == 0) {
            stack.clear();
        } else if (top <= stack.size()) {
            pop(stack.size() - top);
        } else {
            for (int i = 0; i < top - stack.size(); i++) {
                push(LuaValue.NIL);
            }
        }
    }

    public LuaValue getRegistry(String name) {
        return registry.getOrDefault(name, LuaValue.NIL);
    }

    public void setRegistry(String name, LuaValue value) {
        if (value.isnil()) {
            registry.remove(name);
        } else {
            registry.put(name, value);
        }
    }

    public void insert(int i, LuaValue value) {
        stack().add(i, value);
    }

    public void push(LuaValue value) {
        stack().add(value);
    }

    public void pop(int n) {
        List<LuaValue> stack = stack();
        for (int i = 0; i < n && !stack.isEmpty(); i++) {
            stack.remove(stack.size() - 1);
        }
    }

    public LuaValue toLuaValue(int stackIndex) {
        assert !luaStacks.isEmpty();
        List<LuaValue> stack = stack();
        if (stackIndex < 0) {
            stackIndex = stack.size() + stackIndex + 1;
        }
        stackIndex--;
        if (stackIndex < 0 || stackIndex >= stack.size()) {
            return LuaValue.NONE;
        }
        return stack.get(stackIndex);
    }

    public void remove(int index) {
        stack().remove(index);
    }

    public void replace(int index, LuaValue value) {
        stack().set(index, value);
    }
}
