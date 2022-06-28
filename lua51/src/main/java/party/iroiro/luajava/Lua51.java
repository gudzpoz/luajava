package party.iroiro.luajava;

import java.util.concurrent.atomic.AtomicReference;

import static party.iroiro.luajava.Lua51Consts.*;

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
    protected AbstractLua newThread(long L, int id, Lua mainThread) {
        return new Lua51(L, id, mainThread);
    }

    @Override
    public LuaError convertError(int code) {
        switch (code) {
            case 0:
                return LuaError.OK;
            case LUA_YIELD:
                return LuaError.YIELD;
            case LUA_ERRRUN:
                return LuaError.RUNTIME;
            case LUA_ERRSYNTAX:
                return LuaError.SYNTAX;
            case LUA_ERRMEM:
                return LuaError.MEMORY;
            case LUA_ERRERR:
                return LuaError.HANDLER;
            default:
                return null;
        }
    }

    @Override
    public LuaType convertType(int code) {
        switch (code) {
            case LUA_TBOOLEAN:
                return LuaType.BOOLEAN;
            case LUA_TFUNCTION:
                return LuaType.FUNCTION;
            case LUA_TLIGHTUSERDATA:
                return LuaType.LIGHTUSERDATA;
            case LUA_TNIL:
                return LuaType.NIL;
            case LUA_TNONE:
                return LuaType.NONE;
            case LUA_TNUMBER:
                return LuaType.NUMBER;
            case LUA_TSTRING:
                return LuaType.STRING;
            case LUA_TTABLE:
                return LuaType.TABLE;
            case LUA_TTHREAD:
                return LuaType.THREAD;
            case LUA_TUSERDATA:
                return LuaType.USERDATA;
            default:
                return null;
        }
    }
}
