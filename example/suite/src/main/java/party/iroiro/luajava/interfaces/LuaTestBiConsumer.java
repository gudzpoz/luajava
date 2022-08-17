package party.iroiro.luajava.interfaces;

/**
 * BiConsumer drop-in for lower Android versions
 *
 * @param <S> type
 * @param <T> type
 */
public interface LuaTestBiConsumer<S, T> {
    void accept(S s, T t);
}
