package party.iroiro.luajava;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvokeSpecialTest {
    @Test
    public void invokeSpecial() {
        try (AbstractLua L = new Lua51()) {
            Iterator<Object> iterator = new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Object next() {
                    return null;
                }

                @Override
                public void remove() {
                }
            };
            L.push(iterator, Lua.Conversion.SEMI);

            assertThrows(UnsupportedOperationException.class, () -> L.invokeSpecial(iterator,
                    Iterator.class.getDeclaredMethod("remove"),
                    new Object[0]
            ));
        }
    }
}
