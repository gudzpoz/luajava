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
                J.pushAll(args);
                int i = JuaAPI.classNew(J.lid, o.m_instance, args.narg() - 1);
                LuaValue value = i == 1 ? J.toLuaValue(-1) : LuaValue.NIL;
                J.pop(args.narg() + i);
                return value;
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
                if (index.isnumber()) {
                    J.pushFrame();
                    int i = JuaAPI.arrayIndex(J.lid, array.m_instance, index.checkint());
                    LuaValue value = i == 1 ? J.toLuaValue(-1) : LuaValue.NIL;
                    J.popFrame();
                    return value;
                }
                return super.call(object, index);
            }
        });
        table.set(NEWINDEX, new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue object, LuaValue index, LuaValue value) {
                JavaArray array = (JavaArray) object;
                LuaJState J = LuaJNatives.instances.get(array.L);
                J.pushFrame();
                J.push(LuaValue.NIL);
                J.push(LuaValue.NIL);
                J.push(value);
                JuaAPI.arrayNewIndex(J.lid, array.m_instance, index.checkint());
                J.popFrame();
                return LuaValue.NIL;
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
            int result = clazz
                    ? JuaAPI.classIndex(J.lid, (Class<?>) o.m_instance, f)
                    : JuaAPI.objectIndex(J.lid, o.m_instance, f);
            if (result == 1) {
                LuaValue value = J.toLuaValue(-1);
                J.pop(1);
                return value;
            } else if (result == 2) {
                return new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        J.pushFrame();
                        J.setError(null);
                        J.pushAll(args);
                        int i = clazz
                                ? JuaAPI.classInvoke(J.lid, (Class<?>) o.m_instance, f, args.narg())
                                : JuaAPI.objectInvoke(J.lid, o.m_instance, f, args.narg() - 1);
                        LuaValue value = i == 1 ? J.toLuaValue(-1) : LuaValue.NIL;
                        J.popFrame();
                        if (J.getError() != null) {
                            return LuaValue.error(J.getError().toString());
                        }
                        return value;
                    }
                };
            }
            return LuaValue.NIL;
        }
    }

    private static class ObjectNewIndex extends ThreeArgFunction {
        private final boolean clazz;

        ObjectNewIndex(boolean clazz) {
            this.clazz = clazz;
        }

        @Override
        public LuaValue call(LuaValue object, LuaValue field, LuaValue value) {
            JavaObject o = (JavaObject) object.checkuserdata(
                    clazz ? JavaClass.class : JavaObject.class
            );
            String f = field.checkjstring();
            LuaJState J = LuaJNatives.instances.get(o.L);
            J.push(null);
            J.push(null);
            J.push(value);
            if (clazz) {
                JuaAPI.classNewIndex(J.lid, (Class<?>) o.m_instance, f);
            } else {
                JuaAPI.objectNewIndex(J.lid, o.m_instance, f);
            }
            J.pop(3);
            return LuaValue.NIL;
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
