module party.iroiro.luajava.jsr223lua {
    exports party.iroiro.luajava.jsr223;

    provides javax.script.ScriptEngineFactory with party.iroiro.luajava.jsr223.LuaScriptEngineFactory;

    requires party.iroiro.luajava;

    requires static party.iroiro.lua51java;
    requires static party.iroiro.lua52java;
    requires static party.iroiro.lua53java;
    requires static party.iroiro.lua54java;
    requires static party.iroiro.luajitjava;
}