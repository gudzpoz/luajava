package party.iroiro.luajava;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import party.iroiro.luajava.util.ClassUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides complex functions for JNI uses
 * <p>
 * Most reflection features on the lua side rely on this class.
 * </p>
 */
public abstract class JuaAPI {
    /**
     * Loads a Lua chunck according with {@link Lua#loadExternal(String)}
     *
     * <p>
     * Used in <code>jmoduleLoad</code> in <code>jni/luajava/juaapi.cpp</code>
     * </p>
     *
     * @param id     see {@link AbstractLua#getInstance(int)}
     * @param module the module name
     * @return 1 if {@link Lua.LuaError#OK}
     */
    public static int load(int id, String module) {
        AbstractLua L = Jua.get(id);
        Lua.LuaError error = L.loadExternal(module);
        if (error != Lua.LuaError.OK) {
            L.push("error " + error + " loading module '" + module + '\'');
        }
        return 1;
    }

    /**
     * Creates a proxy
     *
     * <p>
     * See also the <code>java.proxy</code> API and <code>javaProxy</code> in <code>jni/luajava/lualib.cpp</code>.
     * </p>
     *
     * @param id the Lua state id
     * @return the number of return values pushed on stack (i.e., 1 if successful)
     */
    public static int proxy(int id) {
        Lua L = Jua.get(id);
        int interfaces = L.getTop() - 1;
        LinkedList<Class<?>> classes = new LinkedList<>();
        for (int i = 1; i <= interfaces; i++) {
            Class<?> c = looseGetClass(L, i);
            if (c != null && c.isInterface()) {
                classes.add(c);
            } else {
                L.push("bad argument #" + i + " to 'java.proxy' (expecting an interface)");
                return -1;
            }
        }
        Object o = L.createProxy(classes.toArray(new Class[0]), Lua.Conversion.SEMI);
        L.push(o, Lua.Conversion.NONE);
        return 1;
    }

    /**
     * @param L the Lua state
     * @param i the stack position
     * @return a class converted from the value (jobject, jclass or string) at the stack position
     */
    private static @Nullable Class<?> looseGetClass(Lua L, int i) {
        if (L.isUserdata(i)) {
            Object o = L.toJavaObject(i);
            return o instanceof Class ? ((Class<?>) o) : null;
        } else {
            String name = L.toString(i);
            if (name != null) {
                try {
                    return ClassUtils.forName(name, null);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Pushed onto the stack a jclass or a package
     *
     * <p>
     * See the <code>java.import</code> API and <code>javaImport</code> in <code>jni/luajava/jualib.cpp</code>.
     * </p>
     *
     * @param id        the lua state id
     * @param className name of a class or a package
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int javaImport(int id, String className) {
        Lua L = Jua.get(id);
        if (className.endsWith(".*")) {
            L.createTable(0, 0);
            L.createTable(0, 1);
            String packageName = className.substring(0, className.length() - 1);
            L.push(l -> {
                String name = l.toString(-1);
                if (name != null) {
                    return javaImport(l.getId(), packageName + name);
                } else {
                    L.push("bad argument #1 to 'java.import' (expecting string)");
                    return -1;
                }
            });
            L.setField(-2, "__index");
            L.setMetatable(-2);
            return 1;
        } else {
            try {
                L.pushJavaClass(ClassUtils.forName(className, null));
                return 1;
            } catch (ClassNotFoundException e) {
                L.push(e.toString());
                return -1;
            }
        }
    }

    /**
     * Converts a value on top of the stack into a more Lua-style form
     *
     * @param id the state id
     * @return the number of return values, always 1
     */
    public static int luaify(int id) {
        Lua L = Jua.get(id);
        Object o = L.toJavaObject(-1);
        if (o != null) {
            L.push(o, Lua.Conversion.FULL);
        }
        return 1;
    }

    /**
     * Allocates an id for a thread created on the Lua side
     *
     * @param mainId the main thread id
     * @param ptr    the pointer to the lua state who does not have an id
     * @return an allocated id
     */
    @SuppressWarnings("unused")
    public static int threadNewId(int mainId, long ptr) {
        return AbstractLua.adopt(mainId, ptr);
    }

    /**
     * Obtains the value of a certain field of an object
     *
     * <p>
     * This method is mainly used by the JNI side. See {@code jni/luajava/jua.cpp}.
     * </p>
     *
     * <p>
     * For static fields, use {@link #classIndex(int, Class, String)} instead.
     * </p>
     *
     * @param index  the id of {@link Jua} thread calling this method
     * @param object the object
     * @param name   the name of the field
     * @return 1 if a field is found, 2 otherwise
     * @see #fieldIndex(int, Class, Object, String)
     */
    @SuppressWarnings("unused")
    public static int objectIndex(int index, @NotNull Object object, String name) {
        return fieldIndex(index, object.getClass(), object, name);
    }

    /**
     * Invokes a member method of an object
     *
     * <p>
     * This method is mainly used by the JNI side. See {@code jni/luajava/jua.cpp}.
     * If {@code name} is {@code null}, we assume that the object is a {@link JFunction}.
     * </p>
     *
     * <p>
     * For static fields, use {@link #classInvoke(int, Class, String, int)} instead.
     * </p>
     *
     * @param index      the id of {@link Jua} thread calling this method
     * @param obj        the object
     * @param name       the name of the field
     * @param paramCount number of parameters (on the lua stack)
     * @return the number result pushed on stack
     * @see #methodInvoke(int, Class, Object, String, int)
     */
    public static int objectInvoke(int index, @NotNull Object obj, @Nullable String name, int paramCount) {
        if (name == null) {
            return juaFunctionCall(index, obj, paramCount);
        } else {
            return methodInvoke(index, obj.getClass(), obj, name, paramCount);
        }
    }

    /**
     * Calls a {@link JFunction}
     *
     * @param index   the id of {@link Jua} thread calling this method
     * @param obj     the {@link JFunction} object
     * @param ignored parameter count, but we are not using it
     * @return the number result pushed on stack
     */
    private static int juaFunctionCall(int index, Object obj, int ignored) {
        Lua L = Jua.get(index);
        if (obj instanceof JFunction) {
            return ((JFunction) obj).__call(L);
        } else {
            return 0;
        }
    }

    /**
     * Invokes a member method of an object
     *
     * @param index        the lua state index
     * @param obj          the object
     * @param name         the method name
     * @param notSignature the method signature, comma separated
     * @param paramCount   the parameter count
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int objectInvoke(int index, Object obj, String name,
                                   String notSignature, int paramCount) {
        return methodInvoke(index, obj.getClass(), obj, name, notSignature, paramCount);
    }

    /**
     * Sets a field of an object
     *
     * @param index the lua state index
     * @param obj   the object
     * @param name  the field name
     * @return always 0
     */
    @SuppressWarnings("unused")
    public static int objectNewIndex(int index, Object obj, String name) {
        return fieldNewIndex(index, obj.getClass(), obj, name);
    }

    /**
     * Constructs an instance of a class
     *
     * @param index      the lua state index
     * @param oClazz     the class ({@link Object} typed to manually handle mismatched types)
     * @param paramCount the parameter count
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int classNew(int index, Object oClazz, int paramCount) {
        Class<?> clazz;
        if (oClazz instanceof Class) {
            clazz = ((Class<?>) oClazz);
        } else {
            return 0;
        }
        Lua L = Jua.get(index);
        Object[] objects = new Object[paramCount];
        Constructor<?> constructor = matchMethod(L, clazz.getConstructors(), null, objects);
        if (constructor != null) {
            return construct(L, objects, constructor);
        }
        return 0;
    }

    /**
     * Constructs an instance
     *
     * @param L           the lua state
     * @param objects     the parameters
     * @param constructor the constructor
     * @return the number of values pushed onto the stack
     */
    private static int construct(Lua L, Object[] objects, Constructor<?> constructor) {
        try {
            Object obj = constructor.newInstance(objects);
            L.pushJavaObject(obj);
            return 1;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return 0;
        }
    }

    /**
     * Pushes a static field of a class onto the stack
     *
     * <p>Used by <code>jobjectIndex</code> in <code>jni/luajava/luaapi.cpp</code></p>
     *
     * @param index the lua state index
     * @param clazz the class
     * @param name  the field name
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int classIndex(int index, Class<?> clazz, String name) {
        if (name.equals("class")) {
            Lua L = Jua.get(index);
            L.pushJavaObject(clazz);
            return 1;
        } else {
            return fieldIndex(index, clazz, null, name);
        }
    }

    /**
     * Invokes a static method of a class
     *
     * @param index      the lua state index
     * @param clazz      the class
     * @param name       the method name
     * @param paramCount the parameter count
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int classInvoke(int index, Class<?> clazz, String name, int paramCount) {
        return methodInvoke(index, clazz, null, name, paramCount);
    }

    /**
     * Invokes a static method of a class
     *
     * @param index        the lua state index
     * @param clazz        the class
     * @param name         the method name
     * @param notSignature the signature, comma separated
     * @param paramCount   the parameter count
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int classInvoke(int index, Class<?> clazz, String name,
                                  String notSignature, int paramCount) {
        return methodInvoke(index, clazz, null, name, notSignature, paramCount);
    }

    /**
     * Assigns to a static field
     *
     * @param index the lua state index
     * @param clazz the class
     * @param name  the field name
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int classNewIndex(int index, Class<?> clazz, String name) {
        return fieldNewIndex(index, clazz, null, name);
    }

    /**
     * Pushes a created array onto the stack
     *
     * <p>
     * Use negative <code>size</code> to indicate that we should create a multi-dimensional array,
     * with the dimensions read from the stack.
     * </p>
     *
     * @param index  the lua state index
     * @param oClass the class
     * @param size   the array size
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int arrayNew(int index, Object oClass, int size) {
        Class<?> clazz;
        if (oClass instanceof Class && oClass != Void.TYPE) {
            clazz = ((Class<?>) oClass);
        } else {
            return 0;
        }
        Lua L = Jua.get(index);
        if (size >= 0) {
            L.pushJavaArray(Array.newInstance(clazz, size));
        } else {
            int depth = -size;
            int[] sizes = new int[depth];
            for (int i = size; i <= -1; i++) {
                if (!L.isNumber(i)) {
                    return 0;
                }
                int current = (int) L.toNumber(i);
                if (current < 0) {
                    return 0;
                }
                sizes[i - size] = current;
            }
            L.pushJavaArray(Array.newInstance(clazz, sizes));
        }
        return 1;
    }

    /**
     * Gets an element of an array
     *
     * @param index the lua state index
     * @param obj   the array
     * @param i     the index (lua index, starting from 1)
     * @return the number of values pushed onto the stack
     */
    @SuppressWarnings("unused")
    public static int arrayIndex(int index, Object obj, int i) {
        try {
            Object e = Array.get(obj, i - 1);
            Jua.get(index).push(e, Lua.Conversion.SEMI);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Assigns to an element of an array
     *
     * @param index the lua state index
     * @param obj   the array
     * @param i     the index (lua index, starting from 1)
     * @return the number of values pushed onto the stack
     */
    public static int arrayNewIndex(int index, Object obj, int i) {
        try {
            Lua L = Jua.get(index);
            Array.set(obj, i - 1, L.toObject(L.getTop(), obj.getClass().getComponentType()));
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * @param obj the array
     * @return the array length
     */
    public static int arrayLength(Object obj) {
        try {
            return Array.getLength(obj);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calls the given method <code>{obj}.{name}(... params from stack)</code>
     * <p>
     * Judge param types from the lua stack and pushes results afterwards.
     * The params are expected to be on stack positions from <code>-paramCount</code>
     * to <code>-1</code>. For example, if you have two params, then the first
     * is expected to be at -2, and the second be at -1.
     * </p>
     *
     * @param index      the index of Java-side {@link Jua}
     * @param clazz      the {@link Class}
     * @param obj        the object (nullable when calling static methods)
     * @param name       the method name
     * @param paramCount number of supplied params
     * @return the number result pushed on stack
     */
    public static int methodInvoke(int index, Class<?> clazz, @Nullable Object obj,
                                   String name, int paramCount) {
        Lua L = Jua.get(index);
        /* Storage of converted params */
        Object[] objects = new Object[paramCount];
        Method method = matchMethod(L, clazz.getMethods(), name, objects);
        if (method == null) {
            L.push("no matching method found");
            return -1;
        } else {
            return methodInvoke(L, method, obj, objects);
        }
    }

    /**
     * Invokes a method
     *
     * @param index        the lua state index
     * @param clazz        the class
     * @param obj          the object, {@code null} if calling a static method
     * @param name         the method name
     * @param notSignature method signature, comma separated
     * @param paramCount   the parameter count
     * @return the number of values pushed onto the stack
     */
    public static int methodInvoke(int index, Class<?> clazz, @Nullable Object obj,
                                   String name, String notSignature, int paramCount) {
        Lua L = Jua.get(index);
        if ("new".equals(name)) {
            if (obj == null) {
                Constructor<?> constructor = matchMethod(clazz, notSignature);
                if (constructor != null) {
                    Object[] objects = new Object[paramCount];
                    if (matchMethod(L, new Constructor[]{constructor}, null, objects) != null) {
                        return construct(L, objects, constructor);
                    }
                }
                L.push("no matching constructor found");
                return -1;
            }
            L.push("bad argument to constructor (Class<?> expected, got Object)");
            return -1;
        }
        Method method = matchMethod(clazz, name, notSignature);
        if (method != null) {
            Object[] objects = new Object[paramCount];
            if (matchMethod(L, new Method[]{method}, name, objects) != null) {
                return methodInvoke(L, method, obj, objects);
            }
        }
        L.push("no matching method found");
        return -1;
    }

    /**
     * Invokes a method and pushes the result onto the stack
     *
     * @param L       the lua state
     * @param method  the method
     * @param obj     the object
     * @param objects the parameters
     * @return the number of values pushed onto the stack
     */
    public static int methodInvoke(Lua L, Method method, @Nullable Object obj, Object[] objects) {
        Object ret;
        try {
            ret = method.invoke(obj, objects);
        } catch (IllegalAccessException e) {
            L.push(e.toString());
            return -1;
        } catch (InvocationTargetException e) {
            L.push(e.getCause().toString());
            return -1;
        }
        if (ret == null) {
            return 0;
        } else {
            L.push(ret, Lua.Conversion.SEMI);
            return 1;
        }
    }

    @SuppressWarnings("unused") // Until we finally support varargs
    private static Object[] transformVarArgs(Executable executable, Object[] objects) {
        if (executable.isVarArgs()) {
            int count = executable.getParameterCount();
            Object[] transformed = new Object[count];
            
            Class<?>[] types = executable.getParameterTypes();
            Class<?> component = types[types.length - 1].getComponentType();
            // TODO: Handle primitive types
            Object[] args = (Object[]) Array.newInstance(component, objects.length - count + 1);
            
            System.arraycopy(objects, 0, transformed, 0, count - 1);
            System.arraycopy(objects, count - 1, args, 0, args.length);
            transformed[count - 1] = args;
            return transformed;
        } else {
            return objects;
        }
    }

    /**
     * Tries to fetch field from an object
     * <p>
     * When a matching field is found, it is pushed to the corresponding lua stack
     * </p>
     *
     * @param index  the index of {@link Jua} state
     * @param clazz  the {@link Class}
     * @param object the object
     * @param name   the name of the field / method
     * @return 1 if a field is found, 2 otherwise
     */
    public static int fieldIndex(int index, Class<?> clazz, @Nullable Object object, String name) {
        Lua L;
        try {
            Field field = clazz.getField(name);
            Object obj = field.get(object);
            L = Jua.get(index);
            L.push(obj, Lua.Conversion.SEMI);
            return 1;
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException ignored) {
            return 2;
        }
    }

    /**
     * Assigns to a field
     *
     * @param index  the lua state index
     * @param clazz  the class
     * @param object the object
     * @param name   the field name
     * @return 0
     */
    private static int fieldNewIndex(int index, Class<?> clazz, Object object, String name) {
        try {
            Field field = clazz.getField(name);
            Lua L = Jua.get(index);
            Class<?> type = field.getType();
            Object o = convertFromLua(L, type, 3);
            field.set(object, o);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException ignored) {
        }
        return 0;
    }

    /**
     * Matches methods against values on stack
     *
     * @param L       the lua state
     * @param methods all the methods
     * @param name    the method name
     * @param params  an array to store converted parameters
     * @param <T>     either {@link Method} or {@link Constructor}
     * @return a match method
     */
    @Nullable
    private static <T extends Executable> T matchMethod(Lua L, T[] methods,
                                                        @Nullable String name, Object[] params) {
        for (T method : methods) {
            if (method.getParameterCount() == params.length) {
                if (name == null || name.equals(method.getName())) {
                    Class<?>[] classes = method.getParameterTypes();
                    try {
                        for (int i = 0; i != params.length; ++i) {
                            params[i] = convertFromLua(L, classes[i], -params.length + i);
                        }
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Find a certain constructor
     *
     * @param clazz        the class
     * @param notSignature the signature, comma separated
     * @return the matching constructor
     */
    @Nullable
    private static Constructor<?> matchMethod(Class<?> clazz, String notSignature) {
        Class<?>[] classes = getClasses(notSignature);
        try {
            return clazz.getConstructor(classes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Find a certain method
     *
     * @param clazz        the class
     * @param name         the name of the method
     * @param notSignature the signature, comma separated
     * @return the matching method
     */
    @Nullable
    private static Method matchMethod(Class<?> clazz, String name, String notSignature) {
        Class<?>[] classes = getClasses(notSignature);
        try {
            return clazz.getMethod(name, classes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Converts an element on the lua stack at <code>index</code> to Java
     *
     * @param L     the lua state
     * @param clazz the expected return type
     * @param index a <b>lua</b> index (that is, starts from 1)
     * @return the converted element
     * @throws IllegalArgumentException when unable to convert
     */
    @Nullable
    public static Object convertFromLua(Lua L, Class<?> clazz, int index)
            throws IllegalArgumentException {
        Lua.LuaType type = L.type(index);
        if (type == Lua.LuaType.NIL) {
            if (clazz.isPrimitive()) {
                throw new IllegalArgumentException("Primitive not accepting null values");
            } else {
                return null;
            }
        } else if (type == Lua.LuaType.BOOLEAN) {
            if (clazz == boolean.class || clazz == Boolean.class) {
                return L.toBoolean(index);
            }
        } else if (type == Lua.LuaType.STRING && clazz.isAssignableFrom(String.class)) {
            return L.toString(index);
        } else if (type == Lua.LuaType.NUMBER) {
            if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)) {
                return convertNumber(L.toNumber(index), clazz);
            } else if (Character.class == clazz) {
                return (char) L.toNumber(index);
            } else if (Boolean.class == clazz) {
                return L.toNumber(index) != 0;
            } else if (clazz == Object.class) {
                return L.toNumber(index);
            }
        } else if (type == Lua.LuaType.USERDATA) {
            Object object = L.toJavaObject(index);
            if (object != null && clazz.isAssignableFrom(object.getClass())) {
                return object;
            }
        } else if (type == Lua.LuaType.TABLE) {
            if (clazz.isAssignableFrom(List.class)) {
                return L.toList(index);
            } else if (clazz.isArray() && clazz.getComponentType() == Object.class) {
                return Objects.requireNonNull(L.toList(index)).toArray(new Object[0]);
            } else if (clazz.isAssignableFrom(Map.class)) {
                return L.toMap(index);
            }
        }
        throw new IllegalArgumentException("Unable to convert to " + clazz.getName());
    }

    private static Object convertNumber(double toNumber, Class<?> clazz)
            throws IllegalArgumentException {
        if (clazz.isPrimitive()) {
            if (boolean.class == clazz) {
                return toNumber != 0;
            }
            if (char.class == clazz) {
                return (char) (byte) toNumber;
            } else if (byte.class == clazz) {
                return (byte) toNumber;
            } else if (short.class == clazz) {
                return (short) toNumber;
            } else if (int.class == clazz) {
                return (int) toNumber;
            } else if (long.class == clazz) {
                return (long) toNumber;
            } else if (float.class == clazz) {
                return (float) toNumber;
            } else /* if (double.class == clazz) */ {
                return toNumber;
            }
        } else {
            return convertBoxedNumber(toNumber, clazz);
        }
    }

    private static Number convertBoxedNumber(double toNumber, Class<?> clazz)
            throws IllegalArgumentException {
        if (Byte.class == clazz) {
            return (byte) toNumber;
        } else if (Short.class == clazz) {
            return (short) toNumber;
        } else if (Integer.class == clazz) {
            return (int) toNumber;
        } else if (Long.class == clazz) {
            return (long) toNumber;
        } else if (Float.class == clazz) {
            return (float) toNumber;
        } else if (Double.class == clazz) {
            return toNumber;
        }
        throw new IllegalArgumentException("Unsupported conversion");
    }

    public static Class<?>[] getClasses(String notSignature) {
        if (notSignature == null || notSignature.isEmpty()) {
            return new Class<?>[0];
        }
        return ClassUtils.toClassArray(Arrays.stream(notSignature.split(",")).map(s -> {
            try {
                return ClassUtils.forName(s, null);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList()));
    }
}
