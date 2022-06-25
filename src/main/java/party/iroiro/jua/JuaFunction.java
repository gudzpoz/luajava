package party.iroiro.jua;

/**
 * Java alternative to <code>lua_CFunction</code>
 */
public abstract class JuaFunction {
    protected final Lua L;

    protected JuaFunction(Lua L) {
        this.L = L;
    }

    /**
     * The function part
     *
     * <p>Unlike <code>lua_CFunction</code>, the parameters on stack
     * start from index 2, since the element at index 1 is always the
     * object itself, as is in the __call metamethod.</p>
     *
     * @return the number of return values
     */
    public abstract int __call();

    /**
     * Registers a global name
     *
     * @param name the global name
     */
    public void register(String name) {
        L.push(this, Lua.Conversion.NONE);
        L.setGlobal(name);
    }
}
