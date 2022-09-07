package party.iroiro.luajava;

import java.io.IOException;

@SuppressWarnings("unused")
public class JavaLibTest {
    public static int open(Lua L) {
        L.createTable(0, 1);
        L.push(l -> {
            l.push(1024);
            return 1;
        });
        L.setField(-2, "getNumber");
        return 1;
    }

    public static int close(Lua L) throws IOException {
        throw new IOException();
    }

    private static int no(Lua L) {
        return 0;
    }

    private static long noInt(Lua L) {
        return 0;
    }
}
