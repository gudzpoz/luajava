package party.iroiro.luajava.printproxy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrintProxyTest {
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public void startCapture() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPrintProxy() throws IOException {
        Lua L = new Lua51();
        L.register("print", l -> {
            System.out.println(l.toString(-1));
            return 0;
        });

        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/printTest.lua", L);
        L.pCall(0, Consts.LUA_MULTRET);

        System.out.println("PROXY TEST :");
        Printable p = new ObjPrint();
        p.print("TESTE 1");

        L.getGlobal("luaPrint");
        p = (Printable) L.createProxy(
                new Class[]{Printable.class}, Lua.Conversion.SEMI
        );
        p.print("Teste 2");

        L.close();
    }

    @AfterAll
    public void endCapture() {
        System.setOut(originalOut);
        assertEquals(
                "PROXY TEST :\n" +
                        "Printing from Java1...TESTE 1\n" +
                        "Printing from lua :Teste 2\n",
                outContent.toString()
        );
    }
}
