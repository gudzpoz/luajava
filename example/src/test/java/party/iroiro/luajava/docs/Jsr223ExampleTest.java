package party.iroiro.luajava.docs;

import org.junit.jupiter.api.Test;

public class Jsr223ExampleTest {
    @Test
    public void setPropertyTest() {
// #region setPropertyTest
System.setProperty("luajava.jsr-223", "jit");
// #endregion setPropertyTest
    }
}
