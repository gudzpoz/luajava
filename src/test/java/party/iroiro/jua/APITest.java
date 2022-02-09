package party.iroiro.jua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
public class APITest {
    static int asserted;
    public static void assertTrue(boolean t) {
        asserted++;
        Assertions.assertTrue(t);
    }

    @Test
    public void apiTest() throws Exception {
        asserted = 0;
        int len = (int) (Math.random() * 20 + 10);
        array = new double[len];
        arrays = new double[len][len];
        double sum = 0;
        for (int i = 0; i < len; i++) {
            array[i] = Math.random();
            sum += array[i];
        }
        for (int i = 0; i < len; i++) {
            arrays[i] = array;
        }
        Jua L = new Jua();
        L.openStringLibrary();
        L.push(sum);
        L.setglobal("sum");
        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/apiTest.lua", L);
        assertEquals(0, L.pcall(0, Consts.LUA_MULTRET), () -> L.toString(-1));
        assertEquals(10, asserted);
        assertEquals(1, L.run("System.out:println(instance.testPrivate)"));
        assertEquals(1, L.run("System.out:println(instance.testFriendly)"));
        assertEquals(1, L.run("APITest:assert(false)"));
    }

    private final int testPrivate = 443;
    final int testFriendly = 443;
    public int testPublic = 443;
    public static double[] array;
    public static double[][] arrays;
}
