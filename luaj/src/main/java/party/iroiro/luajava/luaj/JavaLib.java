package party.iroiro.luajava.luaj;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import party.iroiro.luajava.JuaAPI;

public class JavaLib extends TwoArgFunction {
    private final int address;

    public JavaLib(int address) {
        this.address = address;
    }

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaTable lib = new LuaTable();
        lib.set("import", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (!arg.isstring()) {
                    return LuaValue.NIL;
                }
                LuaJState L = LuaJNatives.instances.get(address);
                String clazz = arg.tojstring();
                if (clazz.endsWith(".*")) {
                    String packagePath = clazz.substring(0, clazz.length() - 2);
                    return new PackageImporter(packagePath);
                }
                L.pushFrame();
                if (JuaAPI.javaImport(L.lid, clazz) == -1) {
                    L.popFrame();
                    return LuaValue.error(L.getError().toString());
                }
                LuaValue value = L.toLuaValue(-1);
                L.popFrame();
                return value;
            }
        });
        lib.set("new", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return super.invoke(args);
            }
        });
        env.set("java", lib);
        return lib;
    }

    private class PackageImporter extends LuaTable {
        private final String packagePath;

        public PackageImporter(String packagePath) {
            this.packagePath = packagePath;
        }

        @Override
        public LuaValue get(LuaValue key) {
            String name = key.checkjstring();
            LuaJState L = LuaJNatives.instances.get(address);
            String className = packagePath + "." + name;
            L.pushFrame();
            if (JuaAPI.javaImport(L.lid, className) == -1) {
                L.popFrame();
                return new PackageImporter(className);
            }
            LuaValue value = L.toLuaValue(-1);
            L.popFrame();
            return value;
        }
    }
}
