package party.iroiro.luajava;

/**
 * A wrapper around a Lua error message
 */
public class LuaException extends RuntimeException {
    public LuaException(String message) {
        super(message);
    }
}
