/*
 * Copyright (C) 2003-2007 Kepler Project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package party.iroiro.luajava;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.lua51.Lua51;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static party.iroiro.luajava.Lua.LuaError.OK;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThreadTest {
    private final int count = 100;
    public final static int REPEATED = 40;
    private long startTime;

    @BeforeAll
    public void startCapture() {
        startTime = System.currentTimeMillis();
    }

    @RepeatedTest(REPEATED)
    public void threadTest() throws Exception {
        final Lua L = new Lua51();
        ResourceLoader loader = new ResourceLoader();
        assertEquals(0, loader.load("/tests/threadTest.lua", L));
        assertEquals(OK, L.pCall(0, Consts.LUA_MULTRET), () -> L.toString(-1));
        ArrayList<Thread> threads = new ArrayList<>();
        threads.ensureCapacity(count);

        StringBuilder builder = new StringBuilder();
        L.push(builder, Lua.Conversion.NONE);
        L.setGlobal("stringbuilder");
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
        for (Thread t : threads) {
            t.join();
        }
        L.close();
        assertEquals(
                count,
                Arrays.stream(builder.toString().split("\n")).filter("test"::equals).count()
        );
    }

    @AfterAll
    public void endCapture() {
        System.out.println();
        long time = System.currentTimeMillis() - startTime;
        assertTrue(time >= count * REPEATED);
    }
}
