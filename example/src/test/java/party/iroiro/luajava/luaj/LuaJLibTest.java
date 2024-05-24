package party.iroiro.luajava.luaj;


import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.LuaException;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.LuaTestSuite.assertThrowsLua;

public class LuaJLibTest {
    @Test
    public void testUnsupportedOperation() {
        try (Lua L = new LuaJ()) {
            L.openLibraries();
            assertThrowsLua(L, "return io.tmpfile()", LuaException.LuaError.RUNTIME);
            assertThrowsLua(L, "return io.popen('/bin/ls')", LuaException.LuaError.RUNTIME);
        }
    }

    @Test
    public void testFileOperations() {
        try (Lua L = new LuaJ()) {
            L.openLibraries();
            File file = File.createTempFile("luajava", ".txt");
            String path = file.getAbsolutePath();
            path = path.replace("\\", "\\\\");
            assertFalse(path.contains("'") || path.endsWith("\\"));
            L.run("f = io.open('" + path + "', 'w')");
            L.run("f:setvbuf('line')");
            L.run("f:write('Hello World')");
            L.run("io.flush(f)");
            L.run("io.close(f)");
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                assertEquals("Hello World", r.readLine());
            }

            L.run("f = io.open('" + path + "', 'a')");
            L.run("f:write('!!!')");
            L.run("io.close(f)");
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                assertEquals("Hello World!!!", r.readLine());
            }

            L.run("f = io.open('" + path + "', 'r')");
            assertThrowsLua(L, "f:seek('set', 2)", LuaException.LuaError.RUNTIME);
            L.run("assert(not f:read('*n'))");
            L.run("assert('Hello World!!!' == f:read('*l'))");
            L.run("assert(not f:read('*l'))");
            L.run("io.close(f)");

            L.run("f = io.open('" + path + "', 'r')");
            L.run("for l in f:lines() do assert(l == 'Hello World!!!') end");
            L.run("io.close(f)");

            L.run("f = io.open('" + path + "', 'r')");
            L.run("assert(f:read('*a') == 'Hello World!!!')");
            L.run("io.close(f)");

            L.run("io.input()");
            L.run("io.output()");
            L.run("io.input('" + path + "')");
            L.run("io.output('" + path + "')");

            Files.delete(file.toPath());
        } catch (IOException e) {
            fail(e);
        }
    }
}
