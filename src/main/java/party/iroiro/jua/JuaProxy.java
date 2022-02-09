package party.iroiro.jua;

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
public class JuaProxy implements InvocationHandler {
    private final int ref;
    private final Jua L;

    JuaProxy(int ref, Jua L) {
        this.ref = ref;
        this.L = L;
    }

    @Override
    public Object invoke(Object ignored, Method method, Object[] objects) throws Throwable {
        int top = L.gettop();
        L.refget(ref);
        L.getfield(-1, method.getName());
        L.refget(ref);
        if (objects == null) {
            L.pcall(1, 1);
        } else {
            Arrays.stream(objects).forEach(L::push);
            L.pcall(objects.length + 1, 1);
        }
        try {
            Object o = JuaAPI.convertFromLua(L, method.getReturnType(), -1);;
            L.settop(top);
            return o;
        } catch (IllegalArgumentException e) {
            L.settop(top);
            throw e;
        }
    }
}
