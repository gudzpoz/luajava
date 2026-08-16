package party.iroiro.luajava;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import party.iroiro.luajava.lua51.Lua51;
import party.iroiro.luajava.luaj.LuaJ;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JuaInstanceCollectionTest {
    @Test
    public void juaInstanceCollectionTest() {
        Lua L = new LuaJ();
        LuaInstances<@NonNull Lua> instances = new LuaInstances<>();
        for (int i = 0; i < 10; i++) {
            assertEquals(i, instances.add(L));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(L, instances.get(i));
        }
        assertEquals(10, instances.size());

        instances.remove(9);
        assertEquals(9, instances.size());
        assertEquals(9, instances.add(L));

        instances.remove(0);
        assertEquals(9, instances.size());
        assertEquals(0, instances.add(L));

        for (int i = 0; i < 4; i++) {
            instances.remove(i * 3);
        }
        assertEquals(Arrays.asList(0, 3, 6, 9), Stream.of(
                instances.add(L),
                instances.add(L),
                instances.add(L),
                instances.add(L)
        ).sorted().collect(Collectors.toList()));

        assertEquals(10, instances.add(L));
        assertEquals(11, instances.size());
        LuaInstances.Token<@NonNull Lua> add = instances.add();
        assertEquals(12, instances.size());
        assertThrows(NullPointerException.class, () -> instances.get(add.id));
        add.setter.accept(L);
        assertSame(L, instances.get(add.id));
    }

    @Test
    public void tooManyInstancesTest() throws NoSuchFieldException, IllegalAccessException {
        Field heads = LuaInstances.class.getDeclaredField("freeHeads");
        heads.setAccessible(true);
        AtomicReferenceArray<?> headArray = (AtomicReferenceArray<?>) heads.get(AbstractLua.instances);
        for (int i = 0; i < headArray.length(); i++) {
            headArray.set(i, null);
        }

        Field field = LuaInstances.class.getDeclaredField("nextId");
        field.setAccessible(true);
        AtomicInteger nextId = (AtomicInteger) field.get(AbstractLua.instances);
        int prev = nextId.get();
        nextId.set(Integer.MAX_VALUE);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> new Lua51().close());
        nextId.set(prev);
    }
}
