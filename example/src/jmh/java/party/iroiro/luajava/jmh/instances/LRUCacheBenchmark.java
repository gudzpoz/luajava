package party.iroiro.luajava.jmh.instances;

import org.openjdk.jmh.annotations.*;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class LRUCacheBenchmark {

    private static final AtomicBoolean isSetup = new AtomicBoolean(false);

    @SuppressWarnings("NotNullFieldNotInitialized")
    private LuaJit L;

    @Setup
    public void setup() {
        synchronized (isSetup) {
            if (!isSetup.getAndSet(true)) {
                LuaInstancesAccess.setInstances("CAS");
            }
        }

        L = new LuaJit();
        L.push(1, Lua.Conversion.NONE);
        L.setGlobal("int");
        L.run("int_value = java.method(int, 'intValue', '')");
        L.run("function met_call() for _ = 1, 1000 do int_value() end end");
    }

    @Benchmark
    @Threads(1)
    public void benchmarkMethodCallT1() {
        L.getGlobal("met_call");
        L.pCall(0, 0);
    }

    @Benchmark
    @Threads(8)
    public void benchmarkMethodCallT8() {
        L.getGlobal("met_call");
        L.pCall(0, 0);
    }

    @Benchmark
    @Threads(32)
    public void benchmarkMethodCallT32() {
        L.getGlobal("met_call");
        L.pCall(0, 0);
    }

    @TearDown
    public void tearDown() {
        L.close();
    }

}
