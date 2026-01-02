package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaTable;

/**
 * Represents a Java array in Lua.
 */
public class JavaArray extends JavaObject {
    /**
     * Creates a new JavaArray wrapper.
     *
     * @param o the Java array object
     * @param meta the metatable for this object
     * @param L the Lua state identifier
     */
    public JavaArray(Object o, LuaTable meta, int L) {
        super(o, meta, L);
    }
}
