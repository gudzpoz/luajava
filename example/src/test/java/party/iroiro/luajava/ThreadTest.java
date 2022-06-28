package party.iroiro.luajava;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.annotation.Testable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static party.iroiro.luajava.Lua.LuaError.OK;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThreadTest {
    private final int count = 100;
    public final static int REPEATED = 100;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private long startTime;

    @BeforeAll
    public void startCapture() {
        outContent.reset();
        startTime = System.currentTimeMillis();
        System.setOut(new PrintStream(outContent));
    }

    @RepeatedTest(REPEATED)
    public void threadTest() throws Exception {
        final Lua L = new Lua51();
        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/threadTest.lua", L);
        System.out.println("OK");
        assertEquals(OK, L.pCall(0, Consts.LUA_MULTRET), () -> L.toString(-1));
        ArrayList<Thread> threads = new ArrayList<>();
        threads.ensureCapacity(count);

        for(int i = 0 ;i < count; i++) {
            synchronized (L) {
                L.getGlobal("tb");
                Object runnable = L.createProxy(new Class[] {java.lang.Runnable.class},
                        Lua.Conversion.SEMI);
                Thread thread = new Thread(() -> {
                    synchronized (L) {
                        ((Runnable) runnable).run();
                    }
                });
                /* This crashes:
                 *   Thread thread = new Thread((Runnable) runnable);
                 */
                threads.add(thread);
                thread.start();
            }
        }
        System.out.println("end main");
        for (Thread t : threads) {
            t.join();
        }
        L.close();
    }

    @AfterAll
    public void endCapture() {
        System.setOut(originalOut);
        assertEquals(
                count * REPEATED,
                Arrays.stream(outContent.toString().split("\n")).filter("test"::equals).count()
        );
        System.out.println();
        long time = System.currentTimeMillis() - startTime;
        assertTrue(time >= 3 * count * REPEATED);
    }
}
