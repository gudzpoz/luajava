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

import java.util.ArrayList;
import java.util.List;

public final class LuaFactory {
    private static final List<Lua> states = new ArrayList<Lua>();

    LuaFactory() {}
    
    public synchronized static Lua getExisting(int index) {
        return states.get(index);
    }
    
    public synchronized static int insert(Lua L) {
        int i;

        for (i = 0 ; i < states.size(); i++) {
            Lua state = states.get(i);
            
            if (state != null && (state.getCPtrPeer() == L.getCPtrPeer())) {
                return i;
            }
        }

        i = getNextIndex();

        if (i == -1) {
            states.add(L);
            return states.size() - 1;
        }
        
        states.set(i, L);
        return i;
    }
    
    public synchronized static void remove(int index) {
        states.set(index, null);
    }
    
    private synchronized static int getNextIndex() {
        if (states.size() == 0) return -1;

        for (int i = 0 ; i < states.size(); i++) {
            if (states.get(i) == null) {
                return i;
            }
        }

        return -1;
    }
}