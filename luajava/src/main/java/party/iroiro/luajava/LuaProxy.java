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

import party.iroiro.luajava.cleaner.LuaReferable;
import party.iroiro.luajava.util.ClassUtils;

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
public final class LuaProxy implements InvocationHandler, LuaReferable {
    final int ref;
    final AbstractLua L;
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
        if (L.shouldSynchronize()) {
            synchronized (L.getMainState()) {
                return syncFreeInvoke(object, method, objects);
            }
        } else {
            return syncFreeInvoke(object, method, objects);
        }
    }

    private Object syncFreeInvoke(Object object, Method method, Object[] objects) throws Throwable {
        int top = L.getTop();
        L.refGet(ref);
        L.getField(-1, method.getName());
        if (L.isNil(-1)) {
            L.setTop(top);
            return callDefaultMethod(object, method, objects);
        }
        L.pushJavaObject(object);

        int nResults = method.getReturnType() == Void.TYPE ? 0 : 1;

        if (objects == null) {
            L.pCall(1, nResults);
        } else {
            for (Object o : objects) {
                L.push(o, degree);
            }
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

    private Object callDefaultMethod(Object o, Method method, Object[] objects) throws Throwable {
        if (ClassUtils.isDefault(method)) {
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
        throw new LuaException(LuaException.LuaError.JAVA, "method not implemented: " + method);
    }

    public static boolean methodEquals(Method method, Class<?> returnType,
                                       String name, Class<?>... parameters) {
        return method.getReturnType() == returnType
               && name.equals(method.getName())
               && Arrays.equals(method.getParameterTypes(), parameters);
    }

    @Override
    public int getReference() {
        return ref;
    }
}
