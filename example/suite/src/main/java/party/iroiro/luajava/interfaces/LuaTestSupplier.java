package party.iroiro.luajava.interfaces;

/**
 * Supplier drop-in for lower Android versions
 *
 * @param <T> type
 */
public interface LuaTestSupplier<T> {
    T get();
}
