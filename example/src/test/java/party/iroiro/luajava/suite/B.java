package party.iroiro.luajava.suite;

public interface B {
    default int b() {
        return ((DefaultProxyTest.A) this).a() + 2;
    }
}
