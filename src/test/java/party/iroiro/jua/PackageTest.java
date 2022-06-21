package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class PackageTest {
    @Test
    public void packageTest() {
        packageRequireTest("table", Jua::openTableLibrary);
        packageRequireTest("bit", Jua::openBitLibrary);
        packageRequireTest("debug", Jua::openDebugLibrary);
        packageRequireTest("io", Jua::openIOLibrary);
        packageRequireTest("math", Jua::openMathLibrary);
        packageRequireTest("os", Jua::openOsLibrary);
        packageRequireTest("string", Jua::openStringLibrary);
        packageRequireTest("package", Jua::openPackageLibrary);
    }

    private void packageRequireTest(String name, Consumer<Jua> opener) {
        String s = "local r = require(\"" + name + "\")";
        try (Jua L = new Jua()) {
            assertEquals(1, L.run(s));
            assertNotEquals(-1, L.toString(-1).indexOf("attempt to call global 'require'"));
        }
        if (!name.equals("package")) {
            try (Jua L = new Jua()) {
                L.openPackageLibrary();
                assertEquals(1, L.run(s));
                assertNotEquals(-1, L.toString(-1).indexOf("module '" + name + "' not found"));
            }
        }
        try (Jua L = new Jua()) {
            L.openPackageLibrary();
            opener.accept(L);
            assertEquals(0, L.run(s));
        }
    }
}
