package party.iroiro.luajava.jmh.instances;

import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaInstances;
import party.iroiro.luajava.LuaNatives;

public abstract class LuaInstancesAccess extends AbstractLua {
    LuaInstancesAccess(LuaNatives luaNative) {
        super(luaNative);
    }

    static void setInstances(LuaInstances<AbstractLua> instances) {
        AbstractLua.instances = instances;
    }

    static void setInstances(String type) {
        LuaInstances<AbstractLua> instances;
        switch (type) {
            case "AtomicReference":
                instances = new LuaInstancesAtomic<>();
                break;
            case "CopyOnWrite":
                instances = new LuaInstancesCopyOnWrite<>();
                break;
            case "CAS":
                instances = new LuaInstancesCAS<>();
                break;
            case "synchronized":
                instances = null;
                break;
            default:
                throw new UnsupportedOperationException("unknown type: " + type);
        }
        if (instances != null) {
            LuaInstancesAccess.setInstances(instances);
        }
    }
}
