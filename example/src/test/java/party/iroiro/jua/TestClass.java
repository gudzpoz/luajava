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

package party.iroiro.jua;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testable
public class TestClass {
    private boolean called = false;
    public void setCalled() {
        called = true;
    }

    @Test
    public void testClass() throws Exception {
        Lua L = new Lua51();

        JuaFunction jf = new JuaFunction(L) {
            public int __call() {
                L.push("Returned String");
                setCalled();
                return 1;
            }
        };

        jf.register("javaFuncTest");

        new JuaFunction(L) {
            @Override
            public int __call() {
                boolean val = L.toBoolean(-1);
                assertTrue(val);
                return 0;
            }
        }.register("assert");

        L.run(" f=javaFuncTest(); assert(f == 'Returned String'); ");
        assertTrue(called);

        L.close();
    }
}