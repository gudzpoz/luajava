package party.iroiro.luajava.suite;

import party.iroiro.luajava.Lua;

import static org.junit.Assert.*;

public class InvokeSpecialConversionTest {
    private final Lua L;

    public InvokeSpecialConversionTest(Lua L) {
        this.L = L;
    }

    private interface ToIntInterface {
        default int toInt(boolean i) {
            return i ? 1 : 0;
        }
        default int toInt(char i) {
            return i;
        }
        default int toInt(byte i) {
            return i;
        }
        default int toInt(double i) {
            return (int) i;
        }
        default int toInt(float i) {
            return (int) i;
        }
        default int toInt(int i) {
            return i;
        }
        default int toInt(long i) {
            return (int) i;
        }
        default int toInt(short i) {
            return i;
        }

        default int sum(
                boolean i1,
                char i2,
                byte i3,
                double i4,
                float i5,
                int i6,
                long i7,
                short i8,
                double i9
        ) {
            return toInt(i1) + i2 + i3 + (int) i4 + (int) i5
                    + i6 + (int) i7 + i8 + (int) i9;
        }
    }

    private interface ToValuesInterface {
        default boolean toZ(int i) {
            return i != 0;
        }
        default char toC(int i) {
            return (char) i;
        }
        default byte toB(int i) {
            return (byte) i;
        }
        default double toD(int i) {
            return i;
        }
        default float toF(int i) {
            return i;
        }
        default int toI(int i) {
            return i;
        }
        default long toJ(int i) {
            return i;
        }
        default short toS(int i) {
            return (short) i;
        }
    }

    public void test() {
        paramConvTest();
        returnValueTest();
    }

    private void returnValueTest() {
        L.run("return {}");
        ToValuesInterface v = (ToValuesInterface) L.createProxy(new Class[]{ToValuesInterface.class}, Lua.Conversion.SEMI);
        assertEquals(Character.MAX_VALUE, v.toC(Character.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE, v.toB(Byte.MAX_VALUE));
        assertEquals(Short.MAX_VALUE, v.toS(Short.MAX_VALUE));
        assertTrue(v.toZ(Integer.MAX_VALUE));
        assertFalse(v.toZ(0));
        assertEquals(Short.MAX_VALUE, v.toF(Short.MAX_VALUE), 0.000001);
        assertEquals(Integer.MAX_VALUE, v.toD(Integer.MAX_VALUE), 0.000001);
        assertEquals(Integer.MAX_VALUE, v.toJ(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, v.toI(Integer.MAX_VALUE));
    }

    private void paramConvTest() {
        L.run("return {}");
        ToIntInterface toInt = (ToIntInterface) L.createProxy(new Class[]{ToIntInterface.class}, Lua.Conversion.SEMI);
        assertEquals(1, toInt.toInt(1));
        assertEquals(Integer.MAX_VALUE, toInt.toInt(Integer.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE, toInt.toInt(Byte.MAX_VALUE));
        assertEquals(Short.MAX_VALUE, toInt.toInt(Short.MAX_VALUE));
        assertEquals(0, toInt.toInt(false));
        assertEquals(1, toInt.toInt(true));
        assertEquals('c', toInt.toInt('c'));
        assertEquals(1024, toInt.toInt(1024.1));
        assertEquals(1024, toInt.toInt(1024.1f));
        assertEquals(Integer.MAX_VALUE, toInt.toInt((long) Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, toInt.toInt((long) Integer.MIN_VALUE));

        assertEquals(
                1 + 'c' + 1 + 3 + 7 + 15 + 31 + 63 + 127,
                toInt.sum(true, 'c', (byte) 1, 3, 7, 15, 31, (short) 63, 127.1)
        );
    }
}
