package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;

/**
 * Represents a Java object in Lua.
 */
public class JavaObject extends LuaUserdata {
    /**
     * The Lua state identifier.
     */
    public final int L;

    /**
     * Creates a new JavaObject wrapper.
     *
     * @param o the Java object
     * @param meta the metatable for this object
     * @param L the Lua state identifier
     */
    public JavaObject(Object o, LuaTable meta, int L) {
        super(o, meta);
        this.L = L;
    }

    @Override
    public boolean raweq(LuaUserdata val) {
        return this == val;
    }
}
