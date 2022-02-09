package party.iroiro.jua;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class SimpleTest {
    @Test
    public void simpleTest() {
        Jua L = new Jua();
        L.push("Hello World from Lua");
        L.setglobal("message");
        L.run("print(message)");
        L.run("java.require('java/lang/System').out:println(message)");
        L.dispose();
    }
}
