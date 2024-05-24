package party.iroiro.luajava.value;

import java.util.Map;

public interface LuaTableTrait extends Map<LuaValue, LuaValue> {
    /**
     * Returns the length of the array part of this Lua table.
     *
     * @return the length as would be returned by the Lua {@code #} operator
     */
    int length();

    /**
     * Returns the size of this Lua table.
     *
     * <p>
     * Please note that Lua does not offer a method to get the size of a table,
     * so this function will need to iterate over it to calculate the size.
     * </p>
     *
     * @return the size
     */
    int size();

    /**
     * @param i the index
     * @return {@code thisLuaValue[i]}
     */
    LuaValue get(int i);

    /**
     * @param key the key, either a {@link LuaValue} type or any Java object
     * @return {@code thisLuaValue[key]}
     */
    LuaValue get(Object key);

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
     * Similar to {@link #set(Object, Object)}
     *
     * @param key   the key
     * @param value the value, either a {@link LuaValue} type or any Java object
     * @return the previous value
     */
    LuaValue set(int key, Object value);

    /**
     * Similar to {@link #put(LuaValue, LuaValue)}, but handles other Java types as well
     *
     * @param key   the key, either a {@link LuaValue} type or any Java object
     * @param value the value, either a {@link LuaValue} type or any Java object
     * @return the previous value
     */
    LuaValue set(Object key, Object value);

    /**
     * Performs {@code thisLuaValue[key] = value}
     *
     * @param key   the key
     * @param value the value
     * @return the old value
     */
    LuaValue put(LuaValue key, LuaValue value);

}
