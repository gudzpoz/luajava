package party.iroiro.jua;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThreadTest {
    private final int count = 100;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private long startTime;

    @BeforeAll
    public void startCapture() {
        outContent.reset();
        startTime = System.currentTimeMillis();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void threadTest() throws Exception {
        final Jua L = new Jua();
        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/threadTest.lua", L);
        System.out.println("OK");
        assertEquals(0, L.pcall(0, Consts.LUA_MULTRET), () -> L.toString(-1));
        ArrayList<Thread> threads = new ArrayList<>();
        threads.ensureCapacity(count);

        for(int i = 0 ;i < count; i++) {
            synchronized (L) {
                L.getglobal("tb");
                Object runnable = L.createProxy("java.lang.Runnable");
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
    }

    @AfterAll
    public void endCapture() {
        System.setOut(originalOut);
        assertEquals(
                count,
                Arrays.stream(outContent.toString().split("\n")).filter("test"::equals).count()
        );
        System.out.println();
        assertTrue(System.currentTimeMillis() - startTime > 100 * 100);
    }
}
