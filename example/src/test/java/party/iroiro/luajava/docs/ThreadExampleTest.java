package party.iroiro.luajava.docs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luajit.LuaJit;

public class ThreadExampleTest {
    private static final Lua L = new Lua54();

    @AfterAll
    public static void end() {
L.close();
    }

    @Test
    public void synchronizedTest() {
// #region synchronizedTest
Lua L = getSharedLua();
synchronized (L.getMainState()) {
    L.run("print('operating...')");
}
// #endregion synchronizedTest
    }

    @Test
    public void differentStatesTest() {
// #region differentStatesTest
Lua L = new LuaJit();
Lua J = new LuaJit();
// No synchronization needed at all
new Thread(new Worker(L)).start();
new Thread(new Worker(J)).start();
// #endregion differentStatesTest
    }

    private Lua getSharedLua() {
return L;
    }

    @Test
    public void sameStateTest() {
// #region sameStateTest
Lua mainState = new LuaJit();
Lua subThread = mainState.newThread();
// Now the two workers need some synchronization mechanism
new Thread(new Worker(mainState)).start();
new Thread(new Worker(subThread)).start();
// #endregion sameStateTest
    }

    private static class Worker implements Runnable {
private final Lua L;

public Worker(Lua L) {
    this.L = L;
}

@Override
public void run() {
    L.run("a = a and a + 1 or 1");
}
    }
}
