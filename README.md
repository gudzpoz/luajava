# LuaJava #

[![Build Status](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml/badge.svg)](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Codecov](https://img.shields.io/codecov/c/github/gudzpoz/luajava?label=Coverage)](https://app.codecov.io/gh/gudzpoz/luajava/)
[![Java 8](https://img.shields.io/badge/Java-8-brown)](https://www.oracle.com/java/technologies/java8.html)
[![Maven Central](https://img.shields.io/maven-central/v/party.iroiro.luajava/luajava?color=blue&label=Maven%20Central)](https://mvnrepository.com/search?q=party.iroiro.luajava)

[![Build Status](https://github.com/gudzpoz/luajava/actions/workflows/docs.yml/badge.svg)](https://github.com/gudzpoz/luajava/actions/workflows/docs.yml)
[![Document Version](https://img.shields.io/github/package-json/v/gudzpoz/luajava?filename=docs%2Fpackage.json&label=Documentation)](https://luajava.iroiro.party/)

[![Hello World Example](./docs/.vuepress/public/hello.svg)](https://luajava.iroiro.party/examples/hello-world-mod.html)

- [LuaJava](#luajava)
  - [About](#about)
  - [Platforms and Versions](#platforms-and-versions)
    - [Artifacts](#artifacts)
  - [Java module](#java-module)
    - [Examples](#examples)
    - [More](#more)
  - [Credits](#credits)
  - [License](#license)

## About ##

This is yet another fork of [the original LuaJava](https://github.com/jasonsantos/luajava).

> LuaJava is a scripting tool for Java. The goal of this tool is to allow scripts written in Lua to manipulate components developed in Java. LuaJava allows Java components to be accessed from Lua using the same syntax that is used for accessing Lua's native objects, without any need for declarations or any kind of preprocessing.
>
> LuaJava also allows any Java interface to get implemented in Lua and passed as parameter to any method, and when called, the equivalent function will be called in Lua, the result passed back to Java.
>
> LuaJava is available under the same license as Lua 5.1, that is, it can be used at no cost for both academic and commercial purposes.

Documentation is available at [LuaJava](https://luajava.iroiro.party/) along with Javadoc.
You are also recommended to familiarize yourself with [the Lua C API](https://www.lua.org/manual/5.4/manual.html#4)
since this library is more or less just a thin wrapper and requires some basic understanding of the C API.

## Platforms and Versions ##

<div style="display:flex;justify-content:center">

| Lua 5.1 | Lua 5.2 | Lua 5.3 | Lua 5.4 | Lua 5.5 | LuaJIT      | LuaJ        |
|:-------:|:-------:|:-------:|:-------:|:--------|:-----------:|:-----------:|
| 5.1.5   | 5.2.4   | 5.3.6   | 5.4.8   | 5.5.0   | [`7152e15`] | [LuaJ fork] |

</div>

[`7152e15`]: https://github.com/LuaJIT/LuaJIT/commits/7152e15489d2077cd299ee23e3d51a4c599ab14f

[LuaJ fork]: https://github.com/wagyourtail/luaj

Supported platforms: **Windows**, **Linux**, **MacOS** and **Android**. Compiled against both ARM and x32/x64. Binaries are not yet tested for iOS.

Compiled natives are available for most common platforms. Check out [LuaJava Platforms](https://luajava.iroiro.party/#platforms) for a platform matrix. LuaJ bindings do not need native binaries and should run on all platforms theoretically.

### Artifacts

To include LuaJava into your project, you need to include two artifacts, one for the Java bindings, the other for the compiled native binaries.
(For LuaJ bindings, you don't need the latter one. However, you will need to add [JitPack](https://jitpack.io/) to your repositories.)

```groovy
// Example: LuaJIT with Desktop natives
implementation 'party.iroiro.luajava:luajit:4.1.0'
runtimeOnly 'party.iroiro.luajava:luajit-platform:4.1.0:natives-desktop'
```

Different artifacts are provided for different Lua versions and different platforms. Check out [Getting Started](https://luajava.iroiro.party/getting-started.html) for an overview. Or you may also search in the [Maven Central](https://mvnrepository.com/search?q=party.iroiro.luajava).

Optionally, you may include `party.iroiro.luajava:jsr223` to provide JSR 223 functionalities. (Note that you still need the above artifacts!)

## Java module ##

[The `java` module](https://luajava.iroiro.party/api.html#java-module) provides these functions:

- `array`: Create a Java array.
- `caught`: Return the latest captured Java `Throwable`
- `detach`: Detach the sub-thread from registry to allow for GC
- `import`: Import classes from Java
- `loadlib`: Load a Java method, similar to `package.loadlib`
- `luaify`: Convert Java values to Lua values
- `method`: Provide an alternative way to call Java methods
- `new`: Construct a Java object
- `proxy`: Create a Java object, calls to whose method will be proxied to Lua functions
- `unwrap`: Return the backing table of a proxy object

### Examples

Here is a simple example on how to correctly initialize new Lua instance.
This example will push `message` variable to Lua with value `Hello World from Lua`,
and then print it using the Java `println`.

```java
public static void main(String[] args) {
    try (Lua L = new Lua51()) {
        L.set("message", "Hello World from LuaJava");
        L.run("java.import('java.lang.System').out:println(message)");
    }
}
```

And [a more advanced "Hello World"](https://luajava.iroiro.party/examples/hello-world-mod.html):

```lua
print = java.method(java.import('java.lang.System').out,'println','java.lang.Object')
Ansi = java.import('org.fusesource.jansi.Ansi')
thread = java.import('java.lang.Thread')(function()

    print(Ansi:ansi():render('@|magenta,bold Hello |@'):toString())

end)
thread:start()
```

### More ###

Check out [AWT Example](https://luajava.iroiro.party/examples/awt.html) for a more complex example.

You may also have a look at [our tests](./example/src/test/resources).

## Credits ##

 * [LuaJava](https://github.com/jasonsantos/luajava)
 * [LibGDX](https://github.com/libgdx/libgdx)
 * [jnigen](https://github.com/libgdx/gdx-jnigen)
 * [Nonlua](https://github.com/deathbeam/jua)
 * [LuaJ](https://github.com/wagyourtail/luaj)

## License ##

Code under [./example](./example) is licensed under [GNU General Public License
Version 3](https://www.gnu.org/licenses/gpl-3.0.txt).

Other parts are licensed under [the MIT license](https://opensource.org/licenses/MIT).
The project includes code from other projects, whose licenses may be found at [./LICENSE](./LICENSE).
