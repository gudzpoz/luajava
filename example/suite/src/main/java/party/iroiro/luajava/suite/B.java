package party.iroiro.luajava.suite;

import party.iroiro.luajava.DefaultProxyTest;

public interface B {
    default int b() {
        return ((DefaultProxyTest.A) this).a() + 2;
    }
}
