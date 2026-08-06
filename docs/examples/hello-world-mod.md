# Advanced Hello World

::: tip
You may try this with [the pre-built Console](../console.md).

<ClientOnly><Asciinema :file="$withBase('/hello.cast')" /></ClientOnly>

:::

<<< ../../example/suite/src/main/resources/luajava/ansiThreadedHelloWorld.lua

Note that you must ensure the Lua states are also `synchronized` on the Java side.
See [Thread Safety](https://luajava.iroiro.party/threadsafety.html) for more information.
