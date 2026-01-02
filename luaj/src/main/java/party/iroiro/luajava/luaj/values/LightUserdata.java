package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaUserdata;

/**
 * Represents light userdata in Lua.
 */
public class LightUserdata extends LuaUserdata {
    /**
     * Creates a new LightUserdata.
     *
     * @param pointer the pointer value
     */
    public LightUserdata(long pointer) {
        super(pointer);
    }

    @Override
    public int type() {
        return TLIGHTUSERDATA;
    }

    @Override
    public String typename() {
        return "lightuserdata";
    }
}
