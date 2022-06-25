package party.iroiro.luajava;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Class that implements the InvocationHandler interface.
 * This class is used in the LuaJava's proxy system.
 * When a proxy object is accessed, the method invoked is
 * called from Lua
 * @author Rizzato
 * @author Thiago Ponte
 */
public class LuaProxy implements InvocationHandler {
    private final int ref;
    private final Lua L;
    private final Lua.Conversion degree;

    LuaProxy(int ref, Lua L, Lua.Conversion degree) {
        this.ref = ref;
        this.L = L;
        this.degree = degree;
    }

    @Override
    public Object invoke(Object ignored, Method method, Object[] objects) {
        int top = L.getTop();
        L.refGet(ref);
        L.getField(-1, method.getName());
        L.refGet(ref);

        int nResults = method.getReturnType() == Void.TYPE ? 0 : 1;

        if (objects == null) {
            L.pCall(1, nResults);
        } else {
            Arrays.stream(objects).forEach(o -> L.push(o, degree));
            L.pCall(objects.length + 1, nResults);
        }

        try {
            if (method.getReturnType() == Void.TYPE) {
                L.setTop(top);
                return null;
            } else {
                Object o = JuaAPI.convertFromLua(L, method.getReturnType(), -1);
                L.setTop(top);
                return o;
            }
        } catch (IllegalArgumentException e) {
            L.setTop(top);
            throw e;
        }
    }
}
