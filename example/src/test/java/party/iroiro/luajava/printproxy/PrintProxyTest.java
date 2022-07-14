package party.iroiro.luajava.printproxy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.annotation.Testable;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.Lua51;
import party.iroiro.luajava.ResourceLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrintProxyTest {
    @Test
    public void testPrintProxy() throws IOException {
        StringBuilder output = new StringBuilder();

        Lua L = new Lua51();
        L.register("print", l -> {
            System.out.println(l.toString(-1));
            output.append(l.toString(-1)).append('\n');
            return 0;
        });

        ResourceLoader loader = new ResourceLoader();
        loader.load("/tests/printTest.lua", L);
        L.pCall(0, Consts.LUA_MULTRET);

        System.out.println("PROXY TEST :");
        output.append("PROXY TEST :").append('\n');
        Printable p = new ObjPrint(output);
        p.print("TESTE 1");

        L.getGlobal("luaPrint");
        p = (Printable) L.createProxy(
                new Class[]{Printable.class}, Lua.Conversion.SEMI
        );
        p.print("Teste 2");

        L.close();

        assertEquals(
                "PROXY TEST :\n" +
                        "Printing from Java1...TESTE 1\n" +
                        "Printing from lua :Teste 2\n",
                output.toString()
        );
    }
}
