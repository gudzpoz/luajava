package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaUserdata;

public class LightUserdata extends LuaUserdata {
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
