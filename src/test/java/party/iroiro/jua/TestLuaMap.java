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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Testable
public class TestLuaMap {
    @Test
    public void testMap() throws LuaException {
        Map<Object, Object> table = new HashMap<>();
        table.put("testTable2-1", "testTable2Value");
        table.put("testTable2-2", new Object());

        // test using a java accessed table.
        Map<Object, Object> luaMap = new LuaMap();

        luaMap.put("test", "testValue");
        luaMap.putAll(table);

        assertTrue(luaMap.containsKey("test"));
        assertTrue(luaMap.containsKey("testTable2-1"));

        assertTrue(luaMap.containsValue("testValue"));

        assertEquals(3, luaMap.size());

        luaMap.remove("test");
        assertNull(luaMap.get("test"));

        luaMap.clear();

        assertEquals(luaMap.size(), 0);


        // test using a lua table
        Lua L = new Lua();
        L.setLoader(new ResourceLoader());
        // L.openLibs();
        L.loadFile("/tests/testMap.lua");
        int err = L.pcall(0, Lua.MULTRET, 0);
        if (err != 0) {
            switch (err) {
                case 1:
                    System.out.println("Runtime error. " + L.toString(-1));
                    break;

                case 2:
                    System.out.println("File not found. " + L.toString(-1));
                    break;

                case 3:
                    System.out.println("Syntax error. " + L.toString(-1));
                    break;

                case 4:
                    System.out.println("Memory error. " + L.toString(-1));
                    break;

                default:
                    System.out.println("Error. " + L.toString(-1));
                    break;
            }
        }

        L.get("map");
        luaMap = (Map<Object, Object>) L.pull(-1).createProxy("java.util.Map");
        L.pop(1);

        luaMap.put("test", "testValue");
        luaMap.putAll(table);

        assertTrue(luaMap.containsKey("test"));
        assertTrue(luaMap.containsKey("testTable2-1"));

        assertEquals(luaMap.get("testTable2-2"), table.get("testTable2-2"));

        assertTrue(luaMap.containsValue("testValue"));

        assertEquals(3, luaMap.size());

        luaMap.remove("test");
        assertNull(luaMap.get("test"));

        luaMap.clear();

        assertEquals(luaMap.size(), 0);
    }
}

/**
 * Class that implements a Map that stores the information in Lua
 *
 * @author thiago
 */
class LuaMap implements Map<Object, Object> {
    private Lua L;
    private LuaValue table;

    /**
     * Initializes the Luastate used and the table
     */
    public LuaMap() throws LuaException {
        L = new Lua();
        // L.openLibs();
        L.newTable();
        table = L.pull(-1);
        L.pop(1);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        L.dispose();
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        table.push();
        L.pushNil();

        int n;
        for (n = 0; L.next(-2) != 0; n++) L.pop(1);

        L.pop(2);

        return n;
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        L.newTable();
        table = L.pull(-1);
        L.pop(1);
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        L.push(key);
        LuaValue obj = L.pull(-1);
        L.pop(1);

        LuaValue temp = L.pull(table, obj);

        return !temp.isNil();
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        L.push(value);
        table.push();
        L.pushNil();

        while (L.next(-2) != 0)/* `key' is at index -2 and `value' at index -1 */ {
            if (L.equal(-4, -1)) {
                L.pop(4);
                return true;
            }
            L.pop(1);
        }

        L.pop(3);
        return false;
    }


    /**
     * not implemented
     *
     * @see java.util.Map#values()
     */
    public Collection values() {
        throw new RuntimeException("not implemented");
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t) {
        for (Object key : t.keySet()) {
            put(key, t.get(key));
        }
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        throw new RuntimeException("not implemented");
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        throw new RuntimeException("not implemented");
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        table.push();
        L.push(key);

        L.getTable(-2);

        Object ret = L.toObject(-1);

        L.pop(2);

        return ret;
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        Object ret = get(key);

        table.push();
        L.push(key);
        L.pushNil();

        L.setTable(-3);

        L.pop(2);

        return ret;
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        Object ret = get(key);

        table.push();
        L.push(key);
        L.push(value);

        L.setTable(-3);

        L.pop(1);

        return ret;
    }

}
