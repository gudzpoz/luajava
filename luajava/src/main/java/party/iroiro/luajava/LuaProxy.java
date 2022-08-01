/*
 * Copyright (C) 2022 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package party.iroiro.luajava;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Class that implements the InvocationHandler interface.
 * This class is used in the LuaJava's proxy system.
 * When a proxy object is accessed, the method invoked is
 * called from Lua
 *
 * @author Rizzato
 * @author Thiago Ponte
 */
public class LuaProxy implements InvocationHandler {
    private final int ref;
    private final AbstractLua L;
    private final Lua.Conversion degree;
    private final Class<?>[] interfaces;

    LuaProxy(int ref, AbstractLua L, Lua.Conversion degree, Class<?>[] interfaces) {
        this.ref = ref;
        this.L = L;
        this.degree = degree;
        this.interfaces = interfaces;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        synchronized (L.getMainState()) {
            int top = L.getTop();
            L.refGet(ref);
            L.getField(-1, method.getName());
            if (L.isNil(-1)) {
                L.setTop(top);
                return callDefaultMethod(object, method, objects);
            }
            L.refGet(ref);

            int nResults = method.getReturnType() == Void.TYPE ? 0 : 1;

            Lua.LuaError code;
            if (objects == null) {
                code = L.pCall(1, nResults);
            } else {
                Arrays.stream(objects).forEach(o -> L.push(o, degree));
                code = L.pCall(objects.length + 1, nResults);
            }

            if (code != Lua.LuaError.OK) {
                RuntimeException t = new LuaException(L.toString(-1));
                L.setTop(top);
                throw t;
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

    private Object callDefaultMethod(Object o, Method method, Object[] objects) throws Throwable {
        if (method.isDefault()) {
            return L.invokeSpecial(o, method, objects);
        }
        return callObjectDefault(o, method, objects);
    }

    private Object callObjectDefault(Object o, Method method, Object[] objects) {
        if (methodEquals(method, int.class, "hashCode")) {
            return hashCode();
        }
        if (methodEquals(method, boolean.class, "equals", Object.class)) {
            return o == objects[0];
        }
        if (methodEquals(method, String.class, "toString")) {
            return "LuaProxy" + Arrays.toString(interfaces) + "@" + Integer.toHexString(hashCode());
        }
        throw new LuaException("method not implemented: " + method);
    }

    public static boolean methodEquals(Method method, Class<?> returnType,
                                       String name, Class<?>... parameters) {
        return method.getReturnType() == returnType
               && name.equals(method.getName())
               && Arrays.equals(method.getParameterTypes(), parameters);
    }
}
