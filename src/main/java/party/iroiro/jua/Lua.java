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
     * Push the function onto the stack, converted to a callable element
     *
     * @param function the function to be pushed onto the stack
     */
    void push(@NotNull JFunction function);

    /**
     * Push a class onto the stack, which may be used with `java.new` on the lua side
     * @param clazz the class
     */
    void pushJavaClass(@NotNull Class<?> clazz);

    /**
     * Push the element onto the stack, converted as is to Java objects
     *
     * @param object the element to be pushed onto the stack
     * @throws IllegalArgumentException when argument is {@code null} or an array
     */
    void pushJavaObject(@NotNull Object object) throws IllegalArgumentException;

    /**
     * Push the element onto the stack, converted as is to Java arrays
     *
     * @param array the element to be pushed onto the stack
     * @throws IllegalArgumentException when argument is {@code null} or a non-array object
     */
    void pushJavaArray(@NotNull Object array) throws IllegalArgumentException;

    /* Convert-something (into Java) functions */

    double toNumber(int index) throws IllegalArgumentException;

    boolean toBoolean(int index) throws IllegalArgumentException;

    @NotNull Object toObject(int index) throws IllegalArgumentException;

    @NotNull String toString(int index) throws IllegalArgumentException;

    /**
     * Get the element at the specified stack position, if the element is a Java object / array / class
     *
     * @param index the stack position of the element
     * @throws IllegalArgumentException when the element does not exist or cannot be converted
     */
    @NotNull Object toJavaObject(int index) throws IllegalArgumentException;

    /**
     * Get the element at the specified stack position
     *
     * @param index the stack position of the element
     * @throws IllegalArgumentException when the element does not exist or cannot be converted
     */
    @NotNull Map<?, ?> toMap(int index) throws IllegalArgumentException;

    /**
     * Get the element at the specified stack position
     *
     * @param index the stack position of the element
     * @throws IllegalArgumentException when the element does not exist or cannot be converted
     */
    @NotNull List<?> toList(int index) throws IllegalArgumentException;

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

    int pCall(int nArgs, int nResults);

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

    void next(int n);

    void rawGet(int index);

    void rawGetI(int index, int n);

    void rawSet(int index);

    void rawSetI(int index, int n);

    int ref(int index);

    void unRef(int index, int ref);

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

    void createProxy(Class<?>[] interfaces);

    LuaNative getLuaNative();

    long getPointer();

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
        NIL(Consts.LUA_TNIL),
        STRING(Consts.LUA_TSTRING),
        NUMBER(Consts.LUA_TNUMBER),
        LIGHTUSERDATA(Consts.LUA_TLIGHTUSERDATA),
        USERDATA(Consts.LUA_TUSERDATA),
        TABLE(Consts.LUA_TTABLE),
        THREAD(Consts.LUA_TTHREAD),
        NONE(Consts.LUA_TNONE),
        FUNCTION(Consts.LUA_TFUNCTION);

        private final int i;

        LuaType(int i) {
            this.i = i;
        }

        public int getType() {
            return i;
        }

        private static final HashMap<Integer, LuaType> types = new HashMap<>();

        static {
            for (LuaType t : LuaType.values()) {
                types.put(t.getType(), t);
            }
        }

        public static @Nullable LuaType valueOf(LuaNative l, int i) {
            return types.get(i);
        }
    }

    enum LuaError {
        NONE, SYNTAX, MEMORY, RUNTIME, HANDLER, YIELD,
    }
}
