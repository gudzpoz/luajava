/*******************************************************************************
 * Copyright (c) 2015 Thomas Slusny
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
 ******************************************************************************/

package io.nondev.nonlua;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.StringTokenizer;

public class LuaValue {
    protected int ref;
    protected Lua L;

    protected LuaValue(Lua L, String globalName) {
        this.L = L;
        L.get(globalName);
        registerValue(-1);
        L.pop(1);
    }

    protected LuaValue(LuaValue parent, String name) {
        this.L = parent.getLua();

        parent.push();
        L.push(name);
        L.getTable(-2);
        L.remove(-2);
        registerValue(-1);
        L.pop(1);
    }

    protected LuaValue(LuaValue parent, int index) {
        this.L = parent.getLua();

        parent.push();
        L.push(index);
        L.getTable(-2);
        L.remove(-2);
        registerValue(-1);
        L.pop(1);
    }

    protected LuaValue(LuaValue parent, LuaValue name) {
        L = parent.getLua();
        parent.push();
        name.push();
        L.getTable(-2);
        L.remove(-2);
        registerValue(-1);
        L.pop(1);
    }

    protected LuaValue(Lua L, int index) {
        this.L = L;
        registerValue(index);
    }

    public Lua getLua() {
        return L;
    }

    private void registerValue(int index) {
        L.copy(index);
        ref = L.ref(Lua.REGISTRY);
    }

    protected void finalize() {
        if (L.getCPtrPeer() != 0) L.unRef(Lua.REGISTRY, ref);
    }

    public void push() {
        L.get(Lua.REGISTRY, ref);
    }

    public boolean isNil() {
        push();
        boolean bool = L.isNil(-1);
        L.pop(1);
        return bool;
    }

    public boolean isBoolean() {
        push();
        boolean bool = L.isBoolean(-1);
        L.pop(1);
        return bool;
    }

    public boolean isNumber() {
        push();
        boolean bool = L.isNumber(-1);
        L.pop(1);
        return bool;
    }

    public boolean isString() {
        push();
        boolean bool = L.isString(-1);
        L.pop(1);
        return bool;
    }

    public boolean isFunction() {
        push();
        boolean bool = L.isFunction(-1);
        L.pop(1);
        return bool;
    }

    public boolean isObject() {
        push();
        boolean bool = L.isObject(-1);
        L.pop(1);
        return bool;
        }
    }

    public boolean isTable() {
        push();
        boolean bool = L.isTable(-1);
        L.pop(1);
        return bool;
    }

    public boolean isUserdata() {
        push();
        boolean bool = L.isUserdata(-1);
        L.pop(1);
        return bool;
    }

    public int type() {
        push();
        int type = L.type(-1);
        L.pop(1);
        return type;
    }

    public String typeString() {
        if (isNil()) return "nil";
        if (isBoolean()) return String.valueOf(toBoolean());
        if (isNumber()) return String.valueOf(toNumber());
        if (isString()) return toString();
        if (isFunction()) return "Function";
        if (isObject()) return toObject().toString();
        if (isUserdata()) return "Userdata";
        if (isTable()) return "Table";
        return null; 
    }

    public boolean toBoolean() {
        push();
        boolean bool = L.toBoolean(-1);
        L.pop(1);
        return bool;
    }

    public Number toNumber() {
        push();
        Number num = L.toNumber(-1);
        L.pop(1);
        return num;
    }

    public String toString() {
        push();
        String str = L.toString(-1);
        L.pop(1);
        return str;
    }

    public Object toObject() {
        push();
        Object obj = L.toObject(-1);
        L.pop(1);
        return obj;
    }

    public LuaValue get(String field) {
        return L.pull(this, field);
    }

    public Object[] call(Object[] args, int nres) throws LuaException {
        if (!isFunction() && !isTable() && !isUserdata()) {
            throw new LuaException("Invalid object. Not a function, table or userdata .");
        }

        int top = L.getTop();
        push();
        int nargs;

        if (args != null) {
            nargs = args.length;

            for (int i = 0; i < nargs; i++) {
                Object obj = args[i];
                L.push(obj);
            }
        } else {
            nargs = 0;
        }

        int err = L.pcall(nargs, nres, 0);

        if (err != 0) {
            String str;

            if (L.isString(-1)) {
                str = L.toString(-1);
                L.pop(1);
            }

            if (err == Lua.ERR_RUNTIME) {
                str = "Runtime error. " + str;
            } else if (err == Lua.ERR_MEMORY) {
                str = "Memory allocation error. " + str;
            } else if (err == Lua.ERR_HANDLER) {
                str = "Error while running the error handler function. " + str;
            } else {
                str = "Lua Error code " + err + ". " + str;
            }

            throw new LuaException(str);
        }

        if (nres == Lua.MULTRET) nres = L.getTop() - top;

        if (L.getTop() - top < nres) {
            throw new LuaException("Invalid Number of Results .");
        }

        Object[] res = new Object[nres];

        for (int i = nres; i > 0; i--) {
            res[i - 1] = L.toObject(-1);
            L.pop(1);
        }

        return res;
    }

    public Object call(Object[] args) throws LuaException {
        return call(args, 1)[0];
    }

    public Object createProxy(String implem) throws LuaException {
        if (!isTable()) {
            throw new LuaException("Invalid Object. Must be Table.");
        }

        StringTokenizer st = new StringTokenizer(implem, ",");
        Class[] interfaces = new Class[st.countTokens()];

        try {
            for (int i = 0; st.hasMoreTokens(); i++) {
                interfaces[i] = Class.forName(st.nextToken());
            }
        } catch (ClassNotFoundException e) {
            throw new LuaException(e);
        }

        InvocationHandler handler = new LuaInvocationHandler(this);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, handler);
    }
}