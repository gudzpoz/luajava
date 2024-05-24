local LuaLibOpen = java.loadlib('party.iroiro.luajava.docs.JavaSideExampleModule', 'open')
assert(1024 == LuaLibOpen().getNumber())
