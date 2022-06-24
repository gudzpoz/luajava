package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayTest {
    @Test
    public void arrayTest() {
        try (Lua L = new Lua51()) {
            int[] i = new int[] { 1, 2, 3, 4 };
            L.pushJavaArray(i);
            L.setGlobal("i");
            assertEquals(0, L.run("assert(i[1] == 1)"));
            assertEquals(0, L.run("assert(i[2] == 2)"));
            assertEquals(0, L.run("assert(i[3] == 3)"));
            assertEquals(0, L.run("assert(i[4] == 4)"));
            assertEquals(0, L.run("assert(i[5] == nil)"));

            assertEquals(0, L.run("assert(#i == 4)"));

            assertEquals(0, L.run("i[1] = 100"));
            assertEquals(100, i[0]);
        }
    }
}
