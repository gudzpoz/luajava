/*
 * Copied from Spring Framework: org/springframework/util/ClassUtils.java
 *
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package party.iroiro.luajava.util;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/**
 * Miscellaneous {@code java.lang.Class} utility methods.
 *
 * <p>Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Sebastien Deleuze
 * @since 1.1
 */
public abstract class ClassUtils {

    /**
     * Suffix for array class names: {@code "[]"}.
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * Prefix for internal array class names: {@code "["}.
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";

    /**
     * Prefix for internal non-primitive array class names: {@code "[L"}.
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /**
     * The package separator character: {@code '.'}.
     */
    private static final char PACKAGE_SEPARATOR = '.';

    /**
     * The nested class separator character: {@code '$'}.
     */
    private static final char NESTED_CLASS_SEPARATOR = '$';

    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: {@code Integer.class -> int.class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);

    /**
     * Map with primitive type name as key and corresponding primitive
     * type as value, for example: {@code "int" -> int.class}.
     */
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

    /**
     * Map with common Java language class name as key and corresponding Class as value.
     * Primarily for efficient deserialization of remote invocations.
     */
    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        // Map entry iteration is less expensive to initialize than forEach with lambdas
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            registerCommonClasses(entry.getKey());
        }

        Set<Class<?>> primitiveTypes = new HashSet<>(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class,
                double[].class, float[].class, int[].class, long[].class, short[].class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
                Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
                Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
                Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class,
                Collection.class, List.class, Set.class, Map.class, Map.Entry.class);
        registerOptionalClasses();

        Class<?>[] javaLanguageInterfaceArray = {Serializable.class, Externalizable.class,
                Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
        registerCommonClasses(javaLanguageInterfaceArray);
    }

    /**
     * {@code Class requires API level 24 (current min is 19): java.util.Optional}
     */
    private static void registerOptionalClasses() {
        try {
            registerCommonClasses(Class.forName("java.util.Optional"));
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Register the given common classes with the ClassUtils cache.
     */
    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    /**
     * Reference to an external class loader
     *
     * <p>
     * This library defaults to using the class loader of this class.
     * If this causes problems, you may try to override the default
     * by setting this field to a suitable class loader instance.
     * </p>
     */
    public static volatile ClassLoader DEFAULT_CLASS_LOADER = null;

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    @Nullable
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = DEFAULT_CLASS_LOADER;
        if (cl != null) {
            return cl;
        }
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * Replacement for {@code Class.forName()} that also returns Class instances
     * for primitives (e.g. "int") and array class names (e.g. "String[]").
     * Furthermore, it is also capable of resolving nested class names in Java source
     * style (e.g. "java.lang.Thread.State" instead of "java.lang.Thread$State").
     *
     * @param name        the name of the Class
     * @param classLoader the class loader to use
     *                    (can be {@code null}, which indicates the default class loader)
     * @return a class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError           if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    private static Class<?> forName(String name, @Nullable ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }
        try {
            return Class.forName(name, false, clToUse);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
            int previousDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR, lastDotIndex - 1);
            if (lastDotIndex != -1 && previousDotIndex != -1 && Character.isUpperCase(name.charAt(previousDotIndex + 1))) {
                String nestedClassName =
                        name.substring(0, lastDotIndex) + NESTED_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
                try {
                    return Class.forName(nestedClassName, false, clToUse);
                } catch (ClassNotFoundException ex2) {
                    // Swallow - let original exception get through
                }
            }
            throw ex;
        }
    }

    /**
     * Resolve the given class name as primitive class, if appropriate,
     * according to the JVM's naming rules for primitive classes.
     * <p>Also supports the JVM's internal class names for primitive arrays.
     * Does <i>not</i> support the "[]" suffix notation for primitive arrays;
     * this is only supported by {@link #forName(String, ClassLoader)}.
     *
     * @param name the name of the potentially primitive class
     * @return the primitive class, or {@code null} if the name does not denote
     * a primitive class or primitive array class
     */
    @Nullable
    public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
        Class<?> result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 7) {
            // Could be a primitive - likely.
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }

    /**
     * Try to use {@link #forName(String, ClassLoader)} with multiple class loaders
     * <p>
     * {@link #getDefaultClassLoader()} prioritizes {@link Thread#getContextClassLoader()}
     * over {@link Class#getClassLoader()}. However, in some scenarios, the package is
     * loaded over a isolated class-loader with an inappropriate {@link Thread#getContextClassLoader()},
     * making class look-up fail. This method is an attempt to fix that.
     * </p>
     *
     * @param name the name of the Class
     * @return a class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError           if the class file could not be loaded
     * @see #forName(String, ClassLoader)
     */
    public static Class<?> forName(String name) throws ClassNotFoundException {
        try {
            return forName(name, null);
        } catch (ClassNotFoundException ex) {
            try {
                return forName(name, ClassUtils.class.getClassLoader());
            } catch (ClassNotFoundException ex2) {
                try {
                    return forName(name, ClassLoader.getSystemClassLoader());
                } catch (ClassNotFoundException ex3) {
                    // Swallow - let original exception get through
                }
            }
            throw ex;
        }
    }

    private final static Set<String> OBJECT_METHODS;

    static {
        Set<String> methods = new HashSet<>();
        Collections.addAll(methods, "equals", "hashCode", "toString");
        OBJECT_METHODS = Collections.unmodifiableSet(methods);
    }

    /**
     * Returns the method name if the class is considered a functional interface in a wilder sense
     *
     * @param classes interfaces
     * @return {@code null} if not applicable
     */
    public static @Nullable String getLuaFunctionalDescriptor(Class<?>... classes) {
        if (allInterfaces(classes)) {
            Queue<Class<?>> searchQueue = new ArrayDeque<>(1);
            String name = null;
            Collections.addAll(searchQueue, classes);
            while (!searchQueue.isEmpty()) {
                Class<?> aClass = searchQueue.poll();
                for (Method m : aClass.getDeclaredMethods()) {
                    if (Modifier.isAbstract(m.getModifiers())) {
                        String mName = m.getName();
                        if (OBJECT_METHODS.contains(mName)) {
                            continue;
                        }
                        if (name == null) {
                            name = mName;
                        } else if (!name.equals(mName)) {
                            return null;
                        }
                    }
                }
                Collections.addAll(searchQueue, aClass.getInterfaces());
            }
            return name;
        }
        return null;
    }

    private static boolean allInterfaces(Class<?>[] classes) {
        for (Class<?> c : classes) {
            if (!c.isInterface() || c.isAnnotation()) {
                return false;
            }
        }
        return true;
    }

    private static final Method IS_DEFAULT;

    static {
        Method isDefaultMethod;
        try {
            isDefaultMethod = Method.class.getMethod("isDefault");
        } catch (NoSuchMethodException e) {
            isDefaultMethod = null;
        }
        IS_DEFAULT = isDefaultMethod;
    }

    /**
     * Wraps {@code isDefault} method, which is unavailable on lower Android versions
     *
     * <p>
     * {@code Call requires API level 24 (current min is 19): java.lang.reflect.Method#isDefault}
     * </p>
     *
     * @param method the method
     * @return true if the method is a default method
     */
    public static boolean isDefault(Method method) {
        if (IS_DEFAULT == null) {
            return false;
        } else {
            try {
                return (boolean) IS_DEFAULT.invoke(method);
            } catch (Throwable e) {
                return false;
            }
        }
    }
}
