package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaTable;

/**
 * Represents a Java class in Lua.
 */
public class JavaClass extends JavaObject {
    /**
     * Creates a new JavaClass wrapper.
     *
     * @param o the Java class object
     * @param meta the metatable for this object
     * @param L the Lua state identifier
     */
    public JavaClass(Class<?> o, LuaTable meta, int L) {
        super(o, meta, L);
    }
}
