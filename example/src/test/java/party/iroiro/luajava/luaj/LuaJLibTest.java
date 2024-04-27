package party.iroiro.luajava.luaj;


import org.junit.jupiter.api.Test;
import party.iroiro.luajava.Lua;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaError.OK;

public class LuaJLibTest {
    @Test
    public void testUnsupportedOperation() {
        try (Lua L = new LuaJ()) {
            L.openLibraries();
            assertNotEquals(OK, L.run("return io.tmpfile()"));
            assertNotEquals(OK, L.run("return io.popen('/bin/ls')"));
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
            assertEquals(OK, L.run("f = io.open('" + path + "', 'w')"));
            assertEquals(OK, L.run("f:setvbuf('line')"));
            assertEquals(OK, L.run("f:write('Hello World')"));
            assertEquals(OK, L.run("io.flush(f)"));
            assertEquals(OK, L.run("io.close(f)"));
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                assertEquals("Hello World", r.readLine());
            }

            assertEquals(OK, L.run("f = io.open('" + path + "', 'a')"));
            assertEquals(OK, L.run("f:write('!!!')"));
            assertEquals(OK, L.run("io.close(f)"));
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                assertEquals("Hello World!!!", r.readLine());
            }

            assertEquals(OK, L.run("f = io.open('" + path + "', 'r')"));
            assertNotEquals(OK, L.run("f:seek('set', 2)"));
            assertEquals(OK, L.run("assert(not f:read('*n'))"));
            assertEquals(OK, L.run("assert('Hello World!!!' == f:read('*l'))"));
            assertEquals(OK, L.run("assert(not f:read('*l'))"));
            assertEquals(OK, L.run("io.close(f)"));

            assertEquals(OK, L.run("f = io.open('" + path + "', 'r')"));
            assertEquals(OK, L.run("for l in f:lines() do assert(l == 'Hello World!!!') end"));
            assertEquals(OK, L.run("io.close(f)"));

            assertEquals(OK, L.run("f = io.open('" + path + "', 'r')"));
            assertEquals(OK, L.run("assert(f:read('*a') == 'Hello World!!!')"));
            assertEquals(OK, L.run("io.close(f)"));

            assertEquals(OK, L.run("io.input()"));
            assertEquals(OK, L.run("io.output()"));
            assertEquals(OK, L.run("io.input('" + path + "')"));
            assertEquals(OK, L.run("io.output('" + path + "')"));

            Files.delete(file.toPath());
        } catch (IOException e) {
            fail(e);
        }
    }
}
