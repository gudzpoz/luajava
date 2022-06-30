package party.iroiro.luajava;

/**
 * The previous implementation
 */
public abstract class Jua {
    /**
     * Gets a {@link Lua} object by its index
     *
     * @param id the id
     * @return the {@link AbstractLua} of this id
     */
    public static AbstractLua get(int id) {
        return AbstractLua.getInstance(id);
    }
}
