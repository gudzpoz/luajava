package party.iroiro.jua.printproxy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.jua.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrintProxyTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public void startCapture() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPrintProxy() throws IOException {
        Jua L = new Jua();
        new JuaFunction(L) {
            @Override
            public int __call() {
                System.out.println(L.toString(-1));
                return 0;
            }
        }.register("print");

        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/printTest.lua", L);
        L.pcall(0, Consts.LUA_MULTRET);

        System.out.println("PROXY TEST :");
        Printable p = new ObjPrint();
        p.print("TESTE 1");

        L.getglobal("luaPrint");
        p = (Printable) L.createProxy("party.iroiro.jua.printproxy.Printable");
        p.print("Teste 2");

        L.dispose();
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
