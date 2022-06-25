package party.iroiro.jua;

/**
 * Functional alternative to {@link JuaFunction} to allow, for example, lambda grammar
 */
public interface JFunction {
    int __call(Lua L);
}
