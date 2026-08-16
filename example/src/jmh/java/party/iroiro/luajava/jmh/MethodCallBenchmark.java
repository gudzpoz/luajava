package party.iroiro.luajava.jmh;

import org.openjdk.jmh.annotations.*;
import party.iroiro.luajava.Lua;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MethodCallBenchmark {

    @Param({"Lua 5.4", "LuaJIT", "LuaJ"})
    public String lua;

    private Lua L;

    @Setup
    public void setup() {
        L = SimpleBenchmark.getLua(lua);
        L.set("big_int", BigInteger.valueOf(1024));
        L.run("int_value = java.method(big_int, 'intValue', '')");
        L.run("min = java.method(java.import('java.lang.Math'), 'min', 'int,int')");
        L.run("function pure()end");
        L.run("function obj_call() assert(big_int:intValue() == 1024) end");
        L.run("function met_call() assert(int_value() == 1024) end");
        L.run("function min_call() assert(min(1, 2) == 1) end");
    }

    @Benchmark
    public void benchmarkArgsMethodCall() {
        L.getGlobal("min_call");
        L.pCall(0, 0);
    }

    @Benchmark
    public void benchmarkObjectMethodCall() {
        L.getGlobal("obj_call");
        L.pCall(0, 0);
    }

    @Benchmark
    public void benchmarkModuleMethodCall() {
        L.getGlobal("met_call");
        L.pCall(0, 0);
    }

    @Benchmark
    public void benchmarkPurePcall() {
        L.getGlobal("pure");
        L.pCall(0, 0);
    }

    @TearDown
    public void tearDown() {
        L.close();
    }
}
