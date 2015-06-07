/*******************************************************************************
 * Copyright (c) 2015 Thomas Slusny.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package io.nondev.nonlua;

public class LuaConfiguration {
    public LuaLoader loader = new LuaLoader() {
        public String path() {
            return "";
        }
    };

    public LuaLogger logger = new LuaLogger() {
        public void log(String msg) {
            System.out.print(msg);
        }
    };

    public boolean baseLib = true;
    public boolean coroutineLib = true;
    public boolean debugLib = true;
    public boolean ioLib = true;
    public boolean javaLib = true;
    public boolean mathLib = true;
    public boolean osLib = true;
    public boolean packageLib = true;
    public boolean socketLib = true;
    public boolean stringLib = true;
    public boolean tableLib = true;
    public boolean utf8Lib = true;
}