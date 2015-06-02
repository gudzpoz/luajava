/*******************************************************************************
 * Copyright (c) 2003-2007 Kepler Project.
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

/**
 * This class is responsible for instantiating new LuaStates.
 * When a new LuaState is instantiated it is put into a List
 * and an index is returned. This index is registred in Lua
 * and it is used to find the right LuaState when lua calls
 * a Java Function.
 * @author Thomas Slusny
 * @author Thiago Ponte
 */
public final class LuaFactory {
	private static final List states = new ArrayList();

	LuaFactory() {}
	
	/**
	 * Returns a existing instance of Lua
	 * @param index
	 * @return LuaState
	 */
	public synchronized static Lua getExisting(int index) {
		return (Lua)states.get(index);
	}
	
	/**
	 * Receives a existing Lua and checks if it exists in the states list.
	 * If it doesn't exist adds it to the list.
	 * @param L
	 * @return int
	 */
	public synchronized static int insert(Lua L) {
		int i;

		for (i = 0 ; i < states.size() ; i++) {
			Lua state = (Lua)states.get(i);
			
			if (state != null && (state.getCPtrPeer() == L.getCPtrPeer())) {
				return i;
			}
		}

		i = getNextStateIndex();
		states.set(i, L);
		return i;
	}
	
	/**
	 * removes the Lua from the states list
	 * @param index
	 */
	public synchronized static void remove(int index) {
		states.set(index, null);
	}
	
	/**
	 * Get next available index
	 * @return int
	 */
	private synchronized static int getNextIndex() {
		int i;
		for (i = 0 ; i < states.size() && states.get(i) != null; i++);
		return i;
	}
}