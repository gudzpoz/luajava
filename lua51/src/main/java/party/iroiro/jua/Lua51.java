package party.iroiro.jua;

import java.util.concurrent.atomic.AtomicReference;

public class Lua51 extends AbstractLua {
    private final static AtomicReference<Lua51Natives> natives = new AtomicReference<>();

    public Lua51() throws UnsatisfiedLinkError {
        super(getNatives());
    }

    protected Lua51(long L, int id, Lua main) {
        super(main.getLuaNative(), L, id, main);
    }

    private static LuaNative getNatives() throws UnsatisfiedLinkError {
        synchronized (natives) {
            if (natives.get() == null) {
                try {
                    natives.set(new Lua51Natives());
                } catch (IllegalStateException e) {
                    throw new UnsatisfiedLinkError("Unable to find natives or init");
                }
            }
            return natives.get();
        }
    }

    @Override
    protected Lua newThread(long L, int id, Lua mainThread) {
        return new Lua51(L, id, mainThread);
    }
}
