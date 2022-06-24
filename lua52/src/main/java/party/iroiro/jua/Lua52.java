package party.iroiro.jua;

import java.util.concurrent.atomic.AtomicReference;

public class Lua52 extends AbstractLua {
    private final static AtomicReference<Lua52Natives> natives = new AtomicReference<>();

    public Lua52() throws UnsatisfiedLinkError {
        super(getNatives());
    }

    protected Lua52(long L, int id, Lua main) {
        super(main.getLuaNative(), L, id, main);
    }

    private static LuaNative getNatives() throws UnsatisfiedLinkError {
        synchronized (natives) {
            if (natives.get() == null) {
                try {
                    natives.set(new Lua52Natives());
                } catch (IllegalStateException e) {
                    throw new UnsatisfiedLinkError("Unable to find natives or init");
                }
            }
            return natives.get();
        }
    }

    @Override
    protected Lua newThread(long L, int id, Lua mainThread) {
        return new Lua52(L, id, mainThread);
    }
}
