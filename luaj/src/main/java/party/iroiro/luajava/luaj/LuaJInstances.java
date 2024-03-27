package party.iroiro.luajava.luaj;

import party.iroiro.luajava.LuaInstances;

final class LuaJInstances extends LuaInstances<LuaJState> {
    LuaJInstances() {
        super();
    }

    @Override
    protected synchronized Token<LuaJState> add() {
        return super.add();
    }

    @Override
    protected synchronized LuaJState get(int id) {
        return super.get(id);
    }

    @Override
    protected synchronized void remove(int id) {
        super.remove(id);
    }
}
