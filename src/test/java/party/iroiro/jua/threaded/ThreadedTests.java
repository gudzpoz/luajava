package party.iroiro.jua.threaded;

import org.junit.jupiter.api.Test;
import party.iroiro.jua.Jua;
import party.iroiro.jua.JuaJNITests;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadedTests {
    @Test
    public void threadedTests() throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        final int cap = 50;
        threads.ensureCapacity(cap);
        for(int i = 0 ;i < cap; i++) {
            Thread thread = new Thread(new JuaJNITests.JuaTest());
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}
