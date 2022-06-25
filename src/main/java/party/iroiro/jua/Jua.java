package party.iroiro.jua;

/**
 * The previous implementation
 */
public abstract class Jua {
    /**
     * Gets a {@link Lua} object by its index
     *
     * @param id the id
     * @return the {@link Lua} of this id
     */
    public static Lua get(int id) {
        return AbstractLua.getInstance(id);
    }
}
