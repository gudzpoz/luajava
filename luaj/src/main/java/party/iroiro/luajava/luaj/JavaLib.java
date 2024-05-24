package party.iroiro.luajava.luaj;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import party.iroiro.luajava.JuaAPI;
import party.iroiro.luajava.luaj.values.JavaClass;
import party.iroiro.luajava.luaj.values.JavaObject;

public class JavaLib extends TwoArgFunction {
    private final int address;

    public JavaLib(int address) {
        this.address = address;
    }

    public static Varargs checkOrError(LuaJState L, int nRet) {
        if (nRet < 0) {
            LuaValue message = L.toLuaValue(-1);
            L.popFrame();
            Throwable error = L.getError();
            if (error != null) {
                throw new LuaError(LuaJState.unwrapLuaError(error));
            }
            return LuaValue.error(message.tojstring());
        }
        LuaValue[] results = new LuaValue[nRet];
        for (int i = 0; i < nRet; i++) {
            results[i] = L.toLuaValue(-nRet + i);
        }
        L.popFrame();
        return LuaValue.varargsOf(results);
    }

    @Override
    public LuaValue call(LuaValue name, LuaValue env) {
        LuaTable lib = new LuaTable();
        lib.set("array", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaJState L = LuaJNatives.instances.get(address);
                if (!(args.arg1() instanceof JavaObject)) {
                    return LuaValue.error("bad argument #1 to 'java.array': __jclass__ or __jobject__ expected");
                }
                JavaObject o = (JavaObject) args.arg1();
                L.pushFrame();
                L.pushAll(args);
                return checkOrError(L, JuaAPI.arrayNew(
                        L.lid, o.m_instance, args.narg() > 2 ? 1 - args.narg() : args.arg(2).checkint()));
            }
        });
        VarArgFunction javaCaught = new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaJState L = LuaJNatives.instances.get(address);
                Throwable error = L.getError();
                return error == null ? LuaValue.NIL : new JavaObject(error, L.jObjectMetatable, L.address);
            }
        };
        lib.set("caught", javaCaught);
        lib.set("catched", javaCaught);
        lib.set("detach", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaThread thread = arg.checkthread();
                if (thread.isMainThread()) {
                    return LuaValue.error("unable to detach a main state");
                }
                // No op
                return LuaValue.NIL;
            }
        });
        lib.set("import", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaJState L = LuaJNatives.instances.get(address);
                String clazz = arg.checkjstring();
                if (clazz.endsWith(".*")) {
                    int depth = countDepth(clazz);
                    String packagePath = clazz.substring(0, clazz.length() - depth * 2);
                    return new PackageImporter(packagePath);
                }
                L.pushFrame();
                return checkOrError(L, JuaAPI.javaImport(L.lid, clazz)).arg1();
            }
        });
        lib.set("loadlib", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue clazz, LuaValue name) {
                LuaJState L = LuaJNatives.instances.get(address);
                L.pushFrame();
                return checkOrError(L, JuaAPI.loadLib(L.lid, clazz.tojstring(), name.tojstring())).arg1();
            }
        });
        lib.set("luaify", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaJState L = LuaJNatives.instances.get(address);
                L.pushFrame();
                L.pushAll(arg);
                return checkOrError(L, JuaAPI.luaify(L.lid)).arg1();
            }
        });
        lib.set("method", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue object, LuaValue method, LuaValue signature) {
                JavaObject o = (JavaObject) object;
                return new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        LuaJState L = LuaJNatives.instances.get(address);
                        L.pushFrame();
                        L.pushAll(args);
                        String sig = signature.isnil() ? "" : signature.tojstring();
                        return checkOrError(L, o instanceof JavaClass
                                ? JuaAPI.classInvoke(L.lid, (Class<?>) o.m_instance,
                                method.tojstring(), sig, args.narg())
                                : JuaAPI.objectInvoke(L.lid, o.m_instance,
                                method.tojstring(), sig, args.narg()));
                    }
                };
            }
        });
        lib.set("new", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaJState L = LuaJNatives.instances.get(address);
                if (!(args.arg1() instanceof JavaObject)) {
                    return LuaValue.error("bad argument #1 to 'java.new': __jclass__ or __jobject__ expected");
                }
                JavaObject o = (JavaObject) args.arg1();
                if (!(o.m_instance instanceof Class)) {
                    return LuaValue.error("bad argument #1 to 'java.new'");
                }
                L.pushFrame();
                L.pushAll(args);
                return checkOrError(L, JuaAPI.classNew(L.lid, o.m_instance, args.narg() - 1));
            }
        });
        lib.set("proxy", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaJState L = LuaJNatives.instances.get(address);
                L.pushFrame();
                L.pushAll(args);
                return checkOrError(L, JuaAPI.proxy(L.lid));
            }
        });
        lib.set("unwrap", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaJState L = LuaJNatives.instances.get(address);
                if (!(arg instanceof JavaObject)) {
                    return LuaValue.error("bad argument #1 to java.unwrap");
                }
                JavaObject o = (JavaObject) arg;
                L.pushFrame();
                return checkOrError(L, JuaAPI.unwrap(L.lid, o.m_instance)).arg1();
            }
        });
        env.set("java", lib);
        return lib;
    }

    private static int countDepth(String clazz) {
        if (!clazz.endsWith(".*")) {
            return 0;
        }
        return countDepth(clazz.substring(0, clazz.length() - 2)) + 1;
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
            int i = JuaAPI.javaImport(L.lid, className);
            if (i == -1) {
                L.popFrame();
                return new PackageImporter(className);
            }
            return checkOrError(L, i).arg1();
        }
    }
}
