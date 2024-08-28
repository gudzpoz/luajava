package party.iroiro.luajava.jmh;

import org.openjdk.jmh.annotations.*;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.luaj.LuaJ;
import party.iroiro.luajava.luajit.LuaJit;

import java.math.BigInteger;

@State(Scope.Benchmark)
public class MethodCallBenchmark {

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
        L.set("big_int", BigInteger.valueOf(1024));
        L.run("int_value = java.method(big_int, 'intValue', '')");
    }

    @Benchmark
    public void benchmarkObjectMethodCall() {
        L.run("assert(big_int:intValue() == 1024)");
    }

    @Benchmark
    public void benchmarkModuleMethodCall() {
        L.run("assert(int_value() == 1024)");
    }

    @TearDown
    public void tearDown() {
        L.close();
    }
}
