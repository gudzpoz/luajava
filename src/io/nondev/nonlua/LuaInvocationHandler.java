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
import java.lang.reflect.Method;

public class LuaInvocationHandler implements InvocationHandler {
    private LuaValue obj;
    
    public LuaInvocationHandler(LuaValue obj) {
        this.obj = obj;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws LuaException {
        String methodName = method.getName();
        LuaValue func = obj.get(methodName);
        if (func.isNil()) return null;
        
        Class retType = method.getReturnType();
        Object ret;

        if (retType.equals(Void.class) || retType.equals(void.class)) {
            func.call(args , 0);
            ret = null;
        } else {
            ret = func.call(args, 1)[0];
            if(ret != null && ret instanceof Double) {
                ret = LuaUtils.convertNumber((Double) ret, retType);
            }
        }
            
        return ret;
    }
}