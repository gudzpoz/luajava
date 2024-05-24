package party.iroiro.luajava.value;

import party.iroiro.luajava.LuaException;

public interface LuaThread {
    /**
     * Sets a global variable to the given value
     *
     * @param key the global variable name
     * @param value the value
     */
    void set(String key, Object value);

    /**
     * Gets a references to a global object
     *
     * @param globalName the global name
     * @return a reference to the value
     */
    LuaValue get(String globalName);

    /**
     * Registers the function to a global name
     *
     * @param name     the global name
     * @param function the function
     */
    void register(String name, LuaFunction function);

    /**
     * Executes Lua code
     *
     * @param command the command
     * @return the return values
     */
    LuaValue[] eval(String command) throws LuaException;

    /**
     * @return a nil Lua value
     */
    LuaValue fromNull();

    /**
     * @param b the boolean
     * @return a boolean Lua value
     */
    LuaValue from(boolean b);

    /**
     * @param n the number
     * @return a number Lua value
     */
    LuaValue from(double n);

    /**
     * @param n the number
     * @return a number Lua value
     */
    LuaValue from(long n);

    /**
     * @param s the string
     * @return a string Lua value
     */
    LuaValue from(String s);
}
