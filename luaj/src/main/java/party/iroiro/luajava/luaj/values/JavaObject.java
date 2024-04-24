package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;

public class JavaObject extends LuaUserdata {
    public final int L;

    public JavaObject(Object o, LuaTable meta, int L) {
        super(o, meta);
        this.L = L;
    }

    @Override
    public boolean raweq(LuaUserdata val) {
        return this == val;
    }
}
