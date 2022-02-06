package party.iroiro.jua;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.annotation.Testable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerThreadTest {
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public void startCapture() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Disabled("Crashes the JVM (quite frequently but not always)")
    @Test
    public void threadTest() throws Exception {
        ArrayList<Thread> threads = new ArrayList<>();
        new Lua();

        for(int i = 0 ;i < 100; i++) {
            Thread thread = new Thread(() -> {
                final Lua L;
                try {
                    L = new Lua();
                    L.setLoader(new ResourceLoader());
                    int err = L.runFile("/tests/perThreadTest.lua");
                    if (err != 0) {
                        System.out.println(L.toString(-1));
                    }
                    L.run("run()");
                    assertEquals(0, err);
                    L.dispose();
                } catch (LuaException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        System.out.println("end main");
        for (Thread t : threads) {
            t.join();
        }
    }

    @AfterAll
    public void endCapture() {
        System.setOut(originalOut);
        /*
        assertEquals(
                100,
                Arrays.stream(outContent.toString().split("\n")).filter("test"::equals).count()
        );
         */
    }
}
