package party.iroiro.jua;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
public class CoroutineTest {
    @Test
    public void coroutineTest() throws IOException {
        Jua L = new Jua();
        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/coTest.lua", L);
        L.pcall(0, Consts.LUA_MULTRET);
        Jua coL = L.newthread();
        int ignored = L.ref();
        coL.getglobal("main");
        int i = 1, j = 1;
        for (int l = 0; l < 36; l++) {
            assertEquals(Consts.LUA_YIELD, coL.resume(0));
            assertEquals(i, coL.toNumber(-1));
            coL.pop(1);
            int k = i + j;
            i = j;
            j = k;
        }
        L.dispose();
    }
}
