# Interactive Console

We build a tiny console application with [JLine3](https://github.com/jline/jline3) every release. You may download the `example-all.jar` from our [Release page](https://github.com/gudzpoz/luajava/releases). Or you can get the lastest snapshot from [our workflow artifacts](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml): choose the lastest build and find the `example` artifact.

The jar bundles desktop natives with it, so you should be about to play around with it on your own computer.

<ClientOnly><Asciinema :file="$withBase('/example.cast')" /></ClientOnly>

`Lua Version` can be any of `5.1`, `5.2`, `5.3`, `5.4`, `luaj` or `jit`.

## Built-In Examples

We bundle several examples with the console JAR, which you may obtain using `require`.

1. `require('luajava.awtTest')()`: Sets up an AWT frame to execute Lua commands.
2. `require('luajava.simpleLuaFile')`: Executes a simple Lua file.
3. `require('luajava.swingTest')()`: Sets up a Swing frame to execute Lua commands.
4. `require('luajava.testMemory')`: Tests garbage collecting a bunch of Java objects and created threads.
5. `require('luajava.wrongLuaFile')`: Tests loading a malformed Lua file.
6. `luajava = require('suite.luajava-compat')`: Wraps the `java` API into the original `luajava` API.

Some of these examples return a function so that you may execute them as many times as you wish:
- `luajava.awtTest`
- `luajava.swingTest`
- `luajava.testMemory`

## Command Line Options

```
Usage: <main class> [-t] [-l=<lua>] [-f=<file> | -e=<expression>]
  -e, --expr=<expression>   The Lua expression to run
  -f, --file=<file>         The Lua file to run
  -l, --lua=<lua>           Specify the Lua version
  -t, --test                Run built-in tests
```
