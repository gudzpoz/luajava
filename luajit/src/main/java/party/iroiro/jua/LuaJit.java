package party.iroiro.jua;

import java.util.concurrent.atomic.AtomicReference;

public class LuaJit extends AbstractLua {
    private final static AtomicReference<LuaJitNatives> natives = new AtomicReference<>();

    public LuaJit() throws UnsatisfiedLinkError {
        super(getNatives());
    }

    protected LuaJit(long L, int id, Lua main) {
        super(main.getLuaNative(), L, id, main);
    }

    private static LuaNative getNatives() throws UnsatisfiedLinkError {
        synchronized (natives) {
            if (natives.get() == null) {
                try {
                    natives.set(new LuaJitNatives());
                } catch (IllegalStateException e) {
                    throw new UnsatisfiedLinkError("Unable to find natives or init");
                }
            }
            return natives.get();
        }
    }

    @Override
    protected Lua newThread(long L, int id, Lua mainThread) {
        return new LuaJit(L, id, mainThread);
    }
}
