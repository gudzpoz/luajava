package party.iroiro.jua;

/**
 * The previous implementation
 */
public abstract class Jua {
    /**
     * Gets a {@link Lua} object by its index
     */
    public static Lua get(int id) {
        return AbstractLua.getInstance(id);
    }
}
