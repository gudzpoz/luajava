package party.iroiro.luajava.interfaces;

/**
 * Function drop-in for lower Android versions
 *
 * @param <S> type one
 * @param <T> type two
 */
public interface LuaTestFunction<S, T> {
    T apply(S s);
}
