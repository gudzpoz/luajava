package party.iroiro.jua;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class JuaInstanceCollectionTest {
    @Test
    public void juaInstanceCollectionTest() {
        Lua jua = mock(Lua.class);
        LuaInstances instances = new LuaInstances();
        for (int i = 0; i < 10; i++) {
            assertEquals(i, instances.add(jua));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(jua, instances.get(i));
        }
        assertEquals(10, instances.size());

        instances.remove(9);
        assertEquals(9, instances.size());
        assertEquals(9, instances.add(jua));

        instances.remove(0);
        assertEquals(9, instances.size());
        assertEquals(0, instances.add(jua));

        for (int i = 0; i < 4; i++) {
            instances.remove(i * 3);
        }
        assertEquals(Arrays.asList(0, 3, 6, 9), Stream.of(
                instances.add(jua),
                instances.add(jua),
                instances.add(jua),
                instances.add(jua)
        ).sorted().collect(Collectors.toList()));

        assertEquals(10, instances.add(jua));
        assertEquals(11, instances.size());
    }
}
