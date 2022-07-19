package party.iroiro.luajava.value;

import org.jetbrains.annotations.Nullable;
import party.iroiro.luajava.Lua;

/**
 * A simple wrapper of references to Lua values
 *
 * <p>
 * You will need to manually close values of complex types. See {@link #close()}.
 * </p>
 */
public interface LuaValue extends AutoCloseable {
    /**
     * @return the type of the Lua value
     */
    Lua.LuaType type();

    /**
     * @return the Lua state
     */
    Lua state();

    /**
     * Pushes the Lua value onto the Lua stack
     */
    void push();

    /**
     * Pushes the Lua value onto the Lua stack of another thread sharing a same main state
     *
     * @param L another thread
     */
    void push(Lua L);

    /**
     * @param i the index
     * @return {@code thisLuaValue[i]}
     */
    LuaValue get(int i);

    /**
     * @param key the key
     * @return {@code thisLuaValue[key]}
     */
    LuaValue get(String key);

    /**
     * @param key the key
     * @return {@code thisLuaValue[key]}
     */
    LuaValue get(LuaValue key);

    /**
     * Performs {@code thisLuaValue[i] = value}
     *
     * @param i     the index
     * @param value the value
     */
    void set(int i, LuaValue value);

    /**
     * Performs {@code thisLuaValue[key] = value}
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, LuaValue value);

    /**
     * Performs {@code thisLuaValue[key] = value}
     *
     * @param key   the key
     * @param value the value
     */
    void set(LuaValue key, LuaValue value);

    /**
     * Performs {@code thisLuaValue(parameter1, parameter2, ...)}
     *
     * @param parameters the parameters
     * @return the return values, {@code null} on error
     */
    @Nullable LuaValue[] call(Object... parameters);

    /**
     * @return a Java value converted from this Lua value
     * @see Lua#toObject(int)
     */
    @Nullable Object toJavaObject();

    /**
     * For reference based values, this clears the reference
     *
     * <p>
     * References might prevent the underlying object from getting garbadge collected.
     * However, for immutable types, we do not rely on references but instead store
     * their values on the Java side, so you do not need to explicitly closing the following types:
     * </p>
     * <ul>
     *     <li>{@link Lua.LuaType#NIL}</li>
     *     <li>{@link Lua.LuaType#NONE}</li>
     *     <li>{@link Lua.LuaType#BOOLEAN}</li>
     *     <li>{@link Lua.LuaType#NUMBER}</li>
     *     <li>{@link Lua.LuaType#STRING}</li>
     * </ul>
     */
    @Override
    void close();
}
