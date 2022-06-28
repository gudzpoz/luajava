package party.iroiro.luajava;

/**
 * Functional alternative to C closures in Lua to allow, for example, lambda grammar
 */
public interface JFunction {
    int __call(Lua L);
}
