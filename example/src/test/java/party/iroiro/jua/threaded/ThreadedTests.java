package party.iroiro.jua.threaded;

import org.junit.jupiter.api.RepeatedTest;
import party.iroiro.jua.APITest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ThreadedTests {
    @RepeatedTest(1)
    public void threadedTests() throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        final int cap = 50;
        threads.ensureCapacity(cap);
        for(int i = 0 ;i < cap; i++) {
            Thread thread = new Thread(() -> {
                APITest test = new APITest();
                assertDoesNotThrow(test::apiTest);
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}
