package party.iroiro.luajava.interfaces;

/**
 * Consumer drop-in for lower Android versions
 *
 * @param <T> type
 */
public interface LuaTestConsumer<T> {
    void accept(T t);
}
