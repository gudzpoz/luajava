# Performance

This page contains some barebone benchmark data and a bit of explanation
to help you understand the costs of crossing between Java and Lua boundaries.

## Benchmark Results

The following benchmarks were run using [JMH (Java Microbenchmark Harness)](https://github.com/openjdk/jmh)
on `jdk17-openjdk` (Linux 6.17.8 x86_64). Results are averaged over multiple iterations.

```
Benchmark                                        (lua)  Mode  Cnt        Score       Error  Units

MethodCallBenchmark.benchmarkPurePcall         Lua 5.4  avgt    3       83.966 ±    25.791  ns/op
MethodCallBenchmark.benchmarkPurePcall          LuaJIT  avgt    3       62.959 ±     1.338  ns/op
MethodCallBenchmark.benchmarkPurePcall            LuaJ  avgt    3       96.250 ±    30.891  ns/op

MethodCallBenchmark.benchmarkObjectMethodCall  Lua 5.4  avgt    3      889.299 ±   116.571  ns/op
MethodCallBenchmark.benchmarkObjectMethodCall   LuaJIT  avgt    3      809.894 ±    34.220  ns/op
MethodCallBenchmark.benchmarkObjectMethodCall     LuaJ  avgt    3      456.171 ±   101.727  ns/op

MethodCallBenchmark.benchmarkModuleMethodCall  Lua 5.4  avgt    3      655.256 ±     3.893  ns/op
MethodCallBenchmark.benchmarkModuleMethodCall   LuaJIT  avgt    3      590.631 ±    10.637  ns/op
MethodCallBenchmark.benchmarkModuleMethodCall     LuaJ  avgt    3      339.211 ±    42.350  ns/op

SimpleBenchmark.benchmarkBinaryTrees           Lua 5.4  avgt    3   370316.187 ±  6141.563  ns/op
SimpleBenchmark.benchmarkBinaryTrees            LuaJIT  avgt    3   215069.889 ±  2591.835  ns/op
SimpleBenchmark.benchmarkBinaryTrees              LuaJ  avgt    3  1284857.526 ± 16054.289  ns/op
```

## Understanding the Numbers

### Java-to-Lua call costs

The `benchmarkPurePcall` benchmarks runs two Java methods: `L.getGlobal("f")` to
push a Lua function onto the Lua stack, and `L.pcall(0, 0)` to call the function.
And it is the bare minimum code you need to call anything in Lua from Java.

Rewriting the code in C with a loop calling `lua_getglobal(...)` and then `lua_pcall(...)`,
each iteration on my machine takes 22.396 ns/op averaged (with LuaJIT).
So the Java JNI costs are mostly 2x ~ 3x pure C.

Note that the costs will go up with increased argument counts:
every argument requires one JNI call to pass them over from Java to Lua.

### Lua-to-Java costs

The `benchmarkObjectMethodCall` benchmarks run Lua code like `big_int:intValue()`
to test Lua-to-Java performance.

Calling from Lua to Java is slower, because Lua is dynamic while Java require static argument types.
So in Lua-to-Java calls, we will need to jump back and forth between Lua and Java
to determine the matching method.
(This, too, is affected by the argument numbers.)

To reduce the dynamics, we provide `java.method` to look up methods
by user-provided signatures, as is benchmarked by `benchmarkModuleMethodCall`.
And it indeed speed up things a little bit.

We can also notice that LuaJ is quite fast in the two benchmark.
It's probably because LuaJ, with all code in Java, has no cross-language boundaries or JNI costs,
and JVM can optimize away some of the costs with method inlining.

### Pure Lua code

The `benchmarkBinaryTrees` benchmarks run a binary-tree related program written in pure Lua.
Unsurprisingly, LuaJIT is fastest. And it is when JNI costs are mostly marginal.

::: tip
The binary tree algorithm used here is quite recursive, which is not that friendly
to LuaJIT's tracing JIT. It should have even greater speed-up with heavily looping,
biased branching Lua programs.
:::

## Take-away

1. JNI calls come with a cost. They are not that costly, but things add up quickly.
   So take note of it if you care about performance.

2. LuaJIT is not specifically faster in use cases where JNI calls are used heavily.
   It is because LuaJIT can't optimize across C/Lua boundaries.

3. Under JNI-heavy use cases, LuaJ is surprisingly more performant, probably because
   the JVM is inlining things away.

4. Finally, LuaJIT is not magic. You will need to make things "mostly Lua" to
   make it perform better.
