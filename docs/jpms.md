# Java 9 Platform Module System

The [Java Platform Module System](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
is a built-in way to express relationships between Java _modules_, starting from Java 9.

To use LuaJava in your Java module, simply place the following `requires` statements in your `module-info.java`:

```java ignored
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    // Replace lua51 with your Lua version: lua51/lua52/lua53/lua54/luajit/luaj
    requires party.iroiro.luajava.lua51;

    // ...
}
```

## Modules

All the modules are either [automatic modules](https://openjdk.org/projects/jigsaw/spec/sotms/#automatic-modules)
or [unnamed modules](https://openjdk.org/projects/jigsaw/spec/sotms/#unnamed-modules).

1. The `party.iroiro.luajava` module is an automatic module, wrapping around the `party.iroiro.luajava:luajava` artifact.
2. The `party.iroiro.luajava.lua*` modules are automatic ones, wrapping Java side bindings for Lua binaries.
3. The native binary JARs are put into the unnamed module.
