package party.iroiro.jua.printproxy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.jua.Lua;
import party.iroiro.jua.LuaException;
import party.iroiro.jua.LuaValue;

import java.io.ByteArrayOutputStream;
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

    static String str =
            "a = 'campo a';" +
                    "b = 'campo b';" +
                    "c = 'campo c';" +
                    "tab = {" +
                    "    a='tab a';" +
                    "    b='tab b';" +
                    "    c='tab c'," +
                    "    d={ e='tab d e' }" +
                    "};" +
                    "function imprime (str) print(str); return 'joao', 1 end;" +
                    "luaPrint = {" +
                    "    implements='org.keplerproject.luajava.test.Printable'," +
                    "    print=function(str) print('Printing from lua :'..str) end" +
                    "}";

    @Test
    public void testPrintProxy() throws LuaException, ClassNotFoundException {
        Lua L = new Lua();
        // L.openBase();

        L.run(str);

        LuaValue func = L.pull("imprime");
        Object[] teste = func.call(new Object[]{"TESTANDO"}, 2);
        System.out.println(teste[0]);
        System.out.println(teste[1]);

        System.out.println("PROXY TEST :");
        Printable p = new ObjPrint();
        p.print("TESTE 1");

        LuaValue o = L.pull("luaPrint");
        p = (Printable) o.createProxy("party.iroiro.jua.printproxy.Printable");
        p.print("Teste 2");

        L.dispose();
    }

    @AfterAll
    public void endCapture() {
        System.setOut(originalOut);
        assertEquals(
                "TESTANDO\t\n" +
                        "joao\n" +
                        "1.0\n" +
                        "PROXY TEST :\n" +
                        "Printing from Java1...TESTE 1\n" +
                        "Printing from lua :Teste 2\t\n",
                outContent.toString()
        );
    }
}
