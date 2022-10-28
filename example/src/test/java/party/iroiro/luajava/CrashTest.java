package party.iroiro.luajava;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;

public class CrashTest {
    @Disabled
    @Test
    public void crashTest() {
        try (Lua L = new Lua51()) {
            L.register("crash", l -> {
                l.rawGetI(-1, 1);
                return 1;
            });
            System.out.println(L.run("print(crash(nil))"));
        }
    }
}
