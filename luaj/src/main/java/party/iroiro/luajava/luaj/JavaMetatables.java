package party.iroiro.luajava.luaj;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import party.iroiro.luajava.JuaAPI;
import party.iroiro.luajava.luaj.values.JavaArray;
import party.iroiro.luajava.luaj.values.JavaClass;
import party.iroiro.luajava.luaj.values.JavaObject;

import static org.luaj.vm2.LuaValue.*;
import static party.iroiro.luajava.luaj.JavaLib.checkOrError;

public abstract class JavaMetatables {
    private static LuaTable objectMetatable(boolean clazz) {
        LuaTable table = tableOf();
        table.set(INDEX, new ObjectIndex(clazz));
        table.set(NEWINDEX, new ObjectNewIndex(clazz));
        table.set(EQ, new ObjectEq());
        return table;
    }

    public static LuaTable objectMetatable() {
        return objectMetatable(false);
    }

    public static LuaTable classMetatable() {
        LuaTable table = objectMetatable(true);
        table.set(CALL, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                JavaClass o = (JavaClass) args.arg1();
                LuaJState J = LuaJNatives.instances.get(o.L);
                J.setError(null);
                J.pushFrame();
                J.pushAll(args);
                return checkOrError(J, JuaAPI.classNew(J.lid, o.m_instance, args.narg() - 1));
            }
        });
        return table;
    }

    public static LuaTable arrayMetatable() {
        LuaTable table = LuaValue.tableOf();
        table.set(LEN, new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                JavaArray array = (JavaArray) arg;
                return LuaValue.valueOf(JuaAPI.arrayLength(array.m_instance));
            }
        });
        table.set(INDEX, new ObjectIndex(false) {
            @Override
            public LuaValue call(LuaValue object, LuaValue index) {
                JavaArray array = (JavaArray) object;
                LuaJState J = LuaJNatives.instances.get(array.L);
                J.setError(null);
                if (index.isnumber()) {
                    J.pushFrame();
                    return checkOrError(J,
                            JuaAPI.arrayIndex(J.lid, array.m_instance, index.checkint())).arg1();
                }
                return super.call(object, index);
            }
        });
        table.set(NEWINDEX, new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue object, LuaValue index, LuaValue value) {
                JavaArray array = (JavaArray) object;
                LuaJState J = LuaJNatives.instances.get(array.L);
                J.setError(null);
                J.pushFrame();
                J.push(LuaValue.NIL);
                J.push(LuaValue.NIL);
                J.push(value);
                return checkOrError(J,
                        JuaAPI.arrayNewIndex(J.lid, array.m_instance, index.checkint())).arg1();
            }
        });
        table.set(EQ, new ObjectEq());
        return table;
    }

    private static class ObjectIndex extends TwoArgFunction {
        private final boolean clazz;

        ObjectIndex(boolean clazz) {
            this.clazz = clazz;
        }

        @Override
        public LuaValue call(LuaValue object, LuaValue field) {
            JavaObject o = (JavaObject) object;
            String f = field.checkjstring();
            LuaJState J = LuaJNatives.instances.get(o.L);
            J.setError(null);
            int result = clazz
                    ? JuaAPI.classIndex(J.lid, (Class<?>) o.m_instance, f)
                    : JuaAPI.objectIndex(J.lid, o.m_instance, f);
            if (result == 1) {
                LuaValue value = J.toLuaValue(-1);
                J.pop(1);
                return value;
            } else /* if (result == 2) */ {
                return new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        J.pushFrame();
                        J.setError(null);
                        if (!(args.arg1() instanceof JavaObject)) {
                            return LuaValue.error("bad argument #1");
                        }
                        J.pushAll(args);
                        int i = clazz
                                ? JuaAPI.classInvoke(J.lid, (Class<?>) o.m_instance, f, args.narg() - 1)
                                : JuaAPI.objectInvoke(J.lid, o.m_instance, f, args.narg() - 1);
                        return checkOrError(J, i);
                    }
                };
            }
        }
    }

    private static class ObjectNewIndex extends ThreeArgFunction {
        private final boolean clazz;

        ObjectNewIndex(boolean clazz) {
            this.clazz = clazz;
        }

        @Override
        public LuaValue call(LuaValue object, LuaValue field, LuaValue value) {
            JavaObject o = (JavaObject) object;
            String f = field.checkjstring();
            LuaJState J = LuaJNatives.instances.get(o.L);
            J.setError(null);
            J.pushFrame();
            J.push(null);
            J.push(null);
            J.push(value);
            int i = clazz
                ? JuaAPI.classNewIndex(J.lid, (Class<?>) o.m_instance, f)
                : JuaAPI.objectNewIndex(J.lid, o.m_instance, f);
            return checkOrError(J, i).arg1();
        }
    }

    private static class ObjectEq extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue object, LuaValue other) {
            Object o = object.checkuserdata();
            if (other instanceof JavaObject) {
                return LuaValue.valueOf(o == ((JavaObject) other).m_instance);
            }
            return LuaValue.FALSE;
        }
    }
}
