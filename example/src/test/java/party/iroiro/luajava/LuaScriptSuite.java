package party.iroiro.luajava;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static party.iroiro.luajava.Lua.LuaError.OK;

public class LuaScriptSuite<T extends AbstractLua> {
    private static final String LUA_ASSERT_THROWS = "function assertThrows(message, fun, ...)\n" +
                                                   "  ok, msg = pcall(fun, ...)\n" +
                                                   "  assert(not ok, debug.traceback('No error while expecting \"' .. message .. '\"'))\n" +
                                                   "  assert(type(msg) == 'string', debug.traceback('Expecting error message on top of the stack'))\n" +
                                                   "  assert(string.find(msg, message) ~= nil, debug.traceback('Expecting \"' .. message .. '\": Received \"' .. msg .. '\"'))\n" +
                                                   "end";
    private final T L;

    public LuaScriptSuite(T L) {
        this.L = L;
        addAssertThrows(L);
    }

    public static void addAssertThrows(Lua L) {
        L.openLibrary("string");
        L.openLibrary("debug");
        assertEquals(OK, L.run(LUA_ASSERT_THROWS), L.toString(-1));
    }

    public static final ScriptTester[] TESTERS = {
            new ScriptTester("/suite/numberConvTest.lua", L -> {
                L.push(new Numbers(), Lua.Conversion.NONE);
                L.setGlobal("numbers");
            }),
            new ScriptTester("/suite/otherConvTest.lua", L -> {
                L.push(new OtherTypes(), Lua.Conversion.NONE);
                L.setGlobal("others");
                LuaNative C = L.getLuaNative();
                if (C instanceof Lua51Natives) {
                    ((Lua51Natives) C).lua_newuserdata(L.getPointer(), 1024);
                } else if (C instanceof Lua52Natives) {
                    ((Lua52Natives) C).lua_newuserdata(L.getPointer(), 1024);
                } else if (C instanceof Lua53Natives) {
                    ((Lua53Natives) C).lua_newuserdata(L.getPointer(), 1024);
                } else if (C instanceof Lua54Natives) {
                    ((Lua54Natives) C).lua_newuserdatauv(L.getPointer(), 1024, 0);
                } else if (C instanceof LuaJitNatives) {
                    ((LuaJitNatives) C).lua_newuserdata(L.getPointer(), 1024);
                } else {
                    fail("Not a supported natives");
                }
                L.setGlobal("myuserdata");
            }),
            new ScriptTester("/suite/proxyTest.lua", L -> {}),
            new ScriptTester("/suite/importTest.lua", L -> {}),
            new ScriptTester("/suite/luaifyTest.lua", L -> {}),
            new ScriptTester("/suite/threadSimpleTest.lua", L -> {}),
            new ScriptTester("/suite/arrayTest.lua", L -> {
                L.pushJavaArray(new int[] {1, 2, 3, 4, 5});
                L.setGlobal("arr");
                assertEquals(-1, JuaAPI.arrayNewIndex(L.getId(), null, 0));
                assertEquals(-1, JuaAPI.arrayLength(""));
            }),
            new ScriptTester("/suite/invokeTest.lua", L -> {
                //noinspection ConstantConditions
                assertEquals(-1, JuaAPI.objectInvoke(L.getId(), null, null, 0));
                assertTrue(Objects.requireNonNull(L.toString(-1)).contains("expecting a JFunction"));
                L.pushJavaClass(AbstractClass.class);
                L.setGlobal("Abstract");
                L.pushJavaClass(PrivateClass.class);
                L.setGlobal("Private");
                L.pushJavaClass(ThrowsClass.class);
                L.setGlobal("Throws");
                assertDoesNotThrow(() ->
                        assertEquals(-1, JuaAPI.methodInvoke(L,
                                PrivateClass.class.getDeclaredMethod("privateFunc"), null, null)));
            }),
            new ScriptTester("/suite/signatureTest.lua", L -> {}),
            new ScriptTester("/suite/indexTest.lua", L -> {
                L.pushJavaClass(StaticClass.class);
                L.setGlobal("Static");
            }),
            new ScriptTester("/suite/moduleTest.lua", L -> {
                L.openLibrary("package");
                L.setExternalLoader(new ClassPathLoader());
            }),
            new ScriptTester("/suite/apiTest.lua", L -> {}),
    };

    public void test() {
        L.openLibrary("coroutine");
        for (ScriptTester tester : TESTERS) {
            assertDoesNotThrow(() -> tester.test(L), tester.file);
        }
    }

    public static class ScriptTester {
        public final String file;
        private final Consumer<AbstractLua> init;

        public ScriptTester(String file, Consumer<AbstractLua> init) {
            this.file = file;
            this.init = init;
        }

        public void test(AbstractLua L) throws IOException {
            init.accept(L);
            ResourceLoader loader = new ResourceLoader();
            loader.load(file, L);
            assertEquals(OK, L.pCall(0, Consts.LUA_MULTRET), () -> L.toString(-1));
        }
    }

    @SuppressWarnings("unused")
    public static class Numbers {
        public char c = 'c';
        public byte b = 1;
        public short s = 2;
        public int i = 3;
        public long l = 4;
        public float f = 5;
        public double d = 6;
        public Character cc = 'C';
        public Byte bb = 7;
        public Short ss = 8;
        public Integer ii = 9;
        public Long ll = 10L;
        public Float ff = 11.f;
        public Double dd = 12.;
        public boolean bool = false;
        public Boolean BOOL = false;
        public BigInteger big = new BigInteger("1024");
    }

    @SuppressWarnings("unused")
    public static class OtherTypes {
        public int i = 1;
        public String s = "2";
        public BigInteger big = new BigInteger("1024");
        public Collection<Object> collection = null;
        public Object[] array1 = null;
        public int[] array2 = null;
        public Map<Object, Object> map = null;
    }

    public static abstract class AbstractClass {
        @SuppressWarnings("unused")
        public static Object returnsNull() { return null; }
    }

    public static class PrivateClass {
        private PrivateClass() {}
        private static void privateFunc() {}
    }

    public static class ThrowsClass {
        public ThrowsClass() throws Exception {
            throw new Exception();
        }
        @SuppressWarnings("unused")
        public static void throwsFunc() throws Exception {
            throw new Exception();
        }
    }

    public static class StaticClass {
        public static int i = 0;
    }
}
