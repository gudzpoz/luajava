package party.iroiro.luajava.interfaces;

/**
 * BiPredicate drop-in for lower Android versions
 *
 * @param <S> type one
 * @param <T> type two
 */
public interface LuaTestBiPredicate<S, T> {
    boolean test(S s, T t);
}
