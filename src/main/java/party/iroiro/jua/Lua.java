package party.iroiro.jua;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.*;

public interface Lua extends AutoCloseable {
    /* Push-something functions */

    /**
     * Push an object onto the stack, converting according to {@link Conversion}.
     *
     * @param object the object to be pushed onto the stack
     * @param degree how the object is converted into lua values
     * @see Conversion
     */
    void push(@Nullable Object object, Conversion degree);

    void pushNil();

    void push(boolean bool);

    void push(@NotNull Number number);

    void push(int integer);

    void push(@NotNull String string);

    /**
     * Push the element onto the stack, converted to lua tables
     *
     * <p>
     * Inner elements are converted with {@link Conversion#FULL}.
     * </p>
     *
     * @param map the element to be pushed onto the stack
     */
    void push(@NotNull Map<?, ?> map);

    /**
     * Push the element onto the stack, converted to lua tables (index starting from 1)
     *
     * <p>
     * Inner elements are converted with {@link Conversion#FULL}.
     * </p>
     *
     * @param collection the element to be pushed onto the stack
     */
    void push(@NotNull Collection<?> collection);

    /**
     * Push an array onto the stack, converted to luatable
     *
     * @param array a array
     * @throws IllegalArgumentException when the object is not array
     */
    void pushArray(@NotNull Object array) throws IllegalArgumentException;

    /**
     * Push the function onto the stack, converted to a callable element
     *
     * @param function the function to be pushed onto the stack
     */
    void push(@NotNull JFunction function);

    /**
     * Push a class onto the stack, which may be used with `java.new` on the lua side
     *
     * @param clazz the class
     */
    void pushJavaClass(@NotNull Class<?> clazz);

    /**
     * Push the element onto the stack, converted as is to Java objects
     *
     * @param object the element to be pushed onto the stack
     * @throws IllegalArgumentException when argument is {@code null} or an array
     */
    void pushJavaObject(@NotNull Object object);

    /**
     * Push the element onto the stack, converted as is to Java arrays
     *
     * @param array the element to be pushed onto the stack
     * @throws IllegalArgumentException when argument is {@code null} or a non-array object
     */
    void pushJavaArray(@NotNull Object array);

    /* Convert-something (into Java) functions */

    double toNumber(int index);

    boolean toBoolean(int index);

    Object toObject(int index);

    Object toObject(int index, Class<?> type);

    @Nullable String toString(int index);

    /**
     * Get the element at the specified stack position, if the element is a Java object / array / class
     *
     * @param index the stack position of the element
     */
    @Nullable Object toJavaObject(int index);

    /**
     * Get the element at the specified stack position
     *
     * @param index the stack position of the element
     */
    @Nullable Map<?, ?> toMap(int index);

    /**
     * Get the element at the specified stack position
     *
     * @param index the stack position of the element
     */
    @Nullable List<?> toList(int index);

    /* Type-checking function */
    boolean isBoolean(int index);

    boolean isFunction(int index);

    /**
     * @param index the element to check type for
     * @return {@code true} if the element is a Java object, a Java class, or a Java array
     */
    boolean isJavaObject(int index);

    boolean isNil(int index);

    boolean isNone(int index);

    boolean isNoneOrNil(int index);

    boolean isNumber(int index);

    boolean isString(int index);

    boolean isTable(int index);

    boolean isThread(int index);

    boolean isUserdata(int index);

    /**
     * @param index the element to inspect
     * @return the lua type of the element, {@code null} if unrecognized (in, for example, incompatible lua versions)
     */
    @Nullable LuaType type(int index);

    /* Measuring functions */
    boolean equal(int i1, int i2);

    int length(int index);

    boolean lessThan(int i1, int i2);

    boolean rawEqual(int i1, int i2);

    /* Other stack manipulation functions */
    int getTop();

    void setTop(int index);

    void insert(int index);

    void pop(int n);

    void pushValue(int index);

    void remove(int index);

    void replace(int index);

    void xMove(Lua other, int n) throws IllegalArgumentException;

    /* Executing functions */
    LuaError load(String script);

    LuaError load(Buffer buffer, String name);

    LuaError run(String script);

    LuaError run(Buffer buffer, String name);

    LuaError pCall(int nArgs, int nResults);

    /* Thread functions */
    Lua newThread();

    LuaError resume(int nArgs);

    LuaError status();

    void yield(int n);

    /* Table functions */
    void createTable(int nArr, int nRec);

    void getField(int index, String key);

    void setField(int index, String key);

    void getTable(int index);

    void setTable(int index);

    int next(int n);

    void rawGet(int index);

    void rawGetI(int index, int n);

    void rawSet(int index);

    void rawSetI(int index, int n);

    int ref(int index);

    int ref();

    void refGet(int ref);

    void unRef(int index, int ref);

    void unref(int ref);

    /* Meta functions */
    void getGlobal(String name);

    void setGlobal(String name);

    int getMetatable(int index);

    void setMetatable(int index);

    int getMetaField(int index, String field);

    void getRegisteredMetatable(String typeName);

    int newRegisteredMetatable(String typeName);

    /* Libraries */
    void openLibraries();

    void openLibrary(String name);

    void concat(int n);

    void error(String message);

    Object createProxy(Class<?>[] interfaces, Conversion degree);

    void register(String name, JFunction function);

    LuaNative getLuaNative();

    long getPointer();

    void addSubThread(Lua lua);

    int getId();

    @Override
    void close();

    /**
     * Controls the degree of conversion from Java to Lua
     */
    enum Conversion {
        /**
         * Converts everything possible, including the following classes:
         *
         * <ul>
         *     <li>Boolean -&gt; boolean</li>
         *     <li>String -&gt; string</li>
         *     <li>Number -&gt; lua_Number</li>
         *     <li>Map / Collection / Array -&gt; table (recursive)</li>
         *     <li>Object -&gt; Java object wrapped by a metatable {@link #pushJavaObject}</li>
         * </ul>
         *
         * <p>
         * Note that this means luatable changes on the lua side will not get reflected
         * to the Java side.
         * </p>
         */
        FULL,
        /**
         * Converts immutable types, including:
         * <ul>
         *     <li>Boolean</li>
         *     <li>String</li>
         *     <li>Number</li>
         * </ul>
         *
         * <p>
         *     {@link Map}, {@link Collection}, etc. are pushed with {@link #pushJavaObject(Object)}.
         *     Arrays are pushed with {@link #pushJavaArray(Object)}.
         * </p>
         */
        SEMI,
        /**
         * All objects, including {@link Integer}, for example, are pushed as either
         * Java objects (with {@link #pushJavaObject(Object)} or Java arrays
         * (with {@link #pushJavaArray(Object)}).
         */
        NONE
    }

    enum LuaType {
        BOOLEAN(),
        FUNCTION(),
        LIGHTUSERDATA(),
        NIL(),
        NONE(),
        NUMBER(),
        STRING(),
        TABLE(),
        THREAD(),
        USERDATA()
    }

    /**
     * Integer values of Lua error codes may vary between version
     */
    enum LuaError {
        /**
         * a file-related error
         */
        FILE,
        /**
         * error while running a __gc metamethod
         */
        GC,
        /**
         * error while running the message handler
         */
        HANDLER,
        /**
         * memory allocation error
         */
        MEMORY,
        /**
         * no errors
         */
        OK,
        /**
         * a runtime error
         */
        RUNTIME,
        /**
         * syntax error during precompilation
         */
        SYNTAX,

        /**
         * the thread (coroutine) yields
         */
        YIELD,
    }
}
