package party.iroiro.luajava.luaj;

import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.LuaException.LuaError;
import party.iroiro.luajava.LuaNatives;

import java.util.concurrent.atomic.AtomicReference;

import static party.iroiro.luajava.luaj.LuaJConsts.*;

/**
 * A thin wrapper around LuaJ to provide Lua C API-like API.
 *
 * <p>
 *     Please note that LuaJ bindings are limited in its capabilities.
 *     Other bindings provided by LuaJava utilizes JNI functions to provide
 *     functionalities like calling default methods in interfaces. However,
 *     LuaJ uses no JNI, and thus won't be able to do things beyond the Java
 *     reflection API.
 * </p>
 */
public class LuaJ extends AbstractLua {
    private final static AtomicReference<LuaJNatives> natives = new AtomicReference<>();

    public LuaJ(long L, int id, AbstractLua mainThread) {
        super(mainThread.getLuaNatives(), L, id, mainThread);
    }

    private static LuaNatives getNatives() {
        synchronized (natives) {
            if (natives.get() == null) {
                natives.set(new LuaJNatives());
            }
            return natives.get();
        }
    }

    public LuaJ() {
        super(getNatives());
    }

    @Override
    protected AbstractLua newThread(long L, int id, AbstractLua mainThread) {
        return new LuaJ(L, id, mainThread);
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
                throw new LuaException(LuaError.RUNTIME, "Unrecognized error code");
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
                throw new LuaException(LuaError.RUNTIME, "Unrecognized type code");
        }
    }

    @Override
    protected boolean shouldSynchronize() {
        return !LuaJNatives.FunctionInvoker.isInsideCoroutine();
    }
}
