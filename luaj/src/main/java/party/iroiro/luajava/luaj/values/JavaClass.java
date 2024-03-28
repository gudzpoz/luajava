package party.iroiro.luajava.luaj.values;

import org.luaj.vm2.LuaTable;

public class JavaClass extends JavaObject {
    public JavaClass(Class<?> o, LuaTable meta, int L) {
        super(o, meta, L);
    }
}
