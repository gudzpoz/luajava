// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package party.iroiro.luajava.util;

import java.lang.reflect.Method;

public abstract class Type {
    /**
     * Returns the descriptor corresponding to the given method.
     *
     * @param method a {@link Method} object.
     * @return the descriptor of the given method.
     */
    public static String getMethodDescriptor(final Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        Class<?>[] parameters = method.getParameterTypes();
        for (Class<?> parameter : parameters) {
            appendDescriptor(parameter, stringBuilder);
        }
        stringBuilder.append(')');
        appendDescriptor(method.getReturnType(), stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * Appends the descriptor of the given class to the given string builder.
     *
     * @param clazz the class whose descriptor must be computed.
     * @param stringBuilder the string builder to which the descriptor must be appended.
     */
    private static void appendDescriptor(final Class<?> clazz, final StringBuilder stringBuilder) {
        Class<?> currentClass = clazz;
        while (currentClass.isArray()) {
            stringBuilder.append('[');
            currentClass = currentClass.getComponentType();
        }
        if (currentClass.isPrimitive()) {
            stringBuilder.append(getPrimitiveDescriptor(currentClass));
        } else {
            stringBuilder.append('L').append(getInternalName(currentClass)).append(';');
        }
    }

    public static char getPrimitiveDescriptor(Class<?> currentClass) {
        if (currentClass == Integer.TYPE) {
            return 'I';
        } else if (currentClass == Void.TYPE) {
            return 'V';
        } else if (currentClass == Boolean.TYPE) {
            return 'Z';
        } else if (currentClass == Byte.TYPE) {
            return 'B';
        } else if (currentClass == Character.TYPE) {
            return 'C';
        } else if (currentClass == Short.TYPE) {
            return 'S';
        } else if (currentClass == Double.TYPE) {
            return 'D';
        } else if (currentClass == Float.TYPE) {
            return 'F';
        } else if (currentClass == Long.TYPE) {
            return 'J';
        } else {
            throw new AssertionError();
        }
    }

    /**
     * Returns the internal name of the given class. The internal name of a class is its fully
     * qualified name, as returned by Class.getName(), where '.' are replaced by '/'.
     *
     * @param clazz an object or array class.
     * @return the internal name of the given class.
     */
    public static String getInternalName(final Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

}
