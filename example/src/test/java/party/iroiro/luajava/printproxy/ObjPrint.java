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

package party.iroiro.luajava.printproxy;

public class ObjPrint implements Printable {
    private final StringBuilder output;
    Printable p;

    public ObjPrint(StringBuilder output) {
        this(null, output);
    }

    public ObjPrint(Printable p, StringBuilder output) {
        this.output = output;
        this.p = p;
    }

    public void print(String str) {
        if (p != null) {
            p.print(str);
        } else {
            System.out.println("Printing from Java1..." + str);
            output.append("Printing from Java1...").append(str).append('\n');
        }
    }

    public void print(String str, int i) {
        if (p != null) {
            p.print(str, 1);
        } else {
            System.out.println("Printing from Java2..." + str);
            output.append("Printing from Java2...").append(str).append('\n');
        }
    }
}
