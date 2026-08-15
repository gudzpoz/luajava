package party.iroiro.luajava.jmh.instances;

import org.openjdk.jmh.annotations.*;
import party.iroiro.luajava.lua51.Lua51;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LuaInstancesWriterBenchmark {

    @Param({"1", "100", "10000"})
    public int instances;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private List<Lua51> allocated;

    @Setup
    public void setup() {
        allocated = new ArrayList<>();
        for (int i = 0; i < this.instances; i++) {
            allocated.add(new Lua51());
        }
    }

    @Benchmark
    @Threads(1)
    public void benchmarkAllocT1() {
        new Lua51().close();
    }
    @Benchmark
    @Threads(8)
    public void benchmarkAllocT8() {
        new Lua51().close();
    }
    @Benchmark
    @Threads(32)
    public void benchmarkAllocT32() {
        new Lua51().close();
    }

    @TearDown
    public void tearDown() {
        for (Lua51 L : allocated) {
            L.close();
        }
    }

}
