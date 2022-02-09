package party.iroiro.jua;

public abstract class JuaFunction {
    protected final Jua L;

    protected JuaFunction(Jua L) {
        this.L = L;
    }

    public abstract int __call();

    public void register(String name) {
        L.push(this);
        L.setglobal(name);
    }
}
