package party.iroiro.luajava.threaded;

import org.junit.jupiter.api.RepeatedTest;
import party.iroiro.luajava.APITest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ThreadedTests {
    @RepeatedTest(1)
    public void threadedTests51() throws InterruptedException {
        threadedTests(() -> {
            APITest test = new APITest();
            assertDoesNotThrow(test::apiTest51);
        });
    }

    @RepeatedTest(1)
    public void threadedTestsJ() throws InterruptedException {
        threadedTests(() -> {
            APITest test = new APITest();
            assertDoesNotThrow(test::apiTestJ);
        });
    }

    public void threadedTests(Runnable r) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        final int cap = 50;
        threads.ensureCapacity(cap);
        for (int i = 0; i < cap; i++) {
            Thread thread = new Thread(r);
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}
