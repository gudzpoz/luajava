package party.iroiro.luajava.jmh;

import org.openjdk.jmh.annotations.*;
import party.iroiro.luajava.ClassPathLoader;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

@State(Scope.Benchmark)
public class SimpleBenchmark {

    private void setupLua(Lua L) {
        L.openLibraries();
        L.run("io.write = function(s) assert(string.find(s, 'tree', 1, true)) end");
        L.setExternalLoader(new ClassPathLoader());
        L.loadExternal("binary-trees");
        L.setGlobal("benchmark");
    }

    @Param({"Lua 5.4", "LuaJIT", "LuaJ"})
    public String lua;

    private Lua L;

    @Setup
    public void setup() {
        switch (lua) {
            case "Lua 5.4":
                L = new Lua54();
                break;
            case "LuaJIT":
                L = new LuaJit();
                break;
            case "LuaJ":
                L = new LuaJ();
                break;
            default:
                throw new IllegalStateException();
        }
        setupLua(L);
    }

    @Benchmark
    public void benchmarkBinaryTrees() {
        L.run("benchmark()");
    }

    @TearDown
    public void tearDown() {
        L.close();
    }
}
