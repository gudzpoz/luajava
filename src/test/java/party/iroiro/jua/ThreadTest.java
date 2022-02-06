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
        final Lua L = new Lua();
        // L.openBase();
        // L.openIo();
        // L.openLibs();
        L.setLoader(new ResourceLoader());
        int err = L.runFile("/tests/threadTest.lua");
        assertEquals(0, err);
        ArrayList<Thread> threads = new ArrayList<>();
        threads.ensureCapacity(100);

        for(int i = 0 ;i < 100; i++) {
            synchronized (L) {
                LuaValue obj = L.pull("tb");
                Object runnable = obj.createProxy("java.lang.Runnable");
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
                100,
                Arrays.stream(outContent.toString().split("\n")).filter("test"::equals).count()
        );
        System.out.println();
        assertTrue(System.currentTimeMillis() - startTime > 100 * 100);
    }
}
