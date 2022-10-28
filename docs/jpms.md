# Java 9 Platform Module System

The [Java Platform Module System](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
is a built-in way to express relationships between Java _modules_, starting from Java 9.

To use LuaJava in your Java module, simply place the follow `requires` statements in your `module-info.java`:

:::: code-group
::: code-group-item *
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.<replace with your Lua version>;

    // ...
}
```
:::
::: code-group-item Lua 5.1
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.lua51;

    // ...
}
```
:::
::: code-group-item Lua 5.2
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.lua52;

    // ...
}
```
:::

::: code-group-item Lua 5.3
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.lua53;

    // ...
}
```
:::

::: code-group-item Lua 5.4
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.lua54;

    // ...
}
```
:::

::: code-group-item LuaJIT
```java
module my.java.module {
    // ...

    requires party.iroiro.luajava;
    requires party.iroiro.luajava.luajit;

    // ...
}
```
:::
::::

## Modules

All the modules are either [automatic modules](https://openjdk.org/projects/jigsaw/spec/sotms/#automatic-modules)
or [unnamed modules](https://openjdk.org/projects/jigsaw/spec/sotms/#unnamed-modules).

1. The `party.iroiro.luajava` module is an automatic module, wrapping around the `party.iroiro.luajava:luajava` artifact.
2. The `party.iroiro.luajava.lua*` modules are automatic ones, wrapping Java side bindings for Lua binaries.
3. The native binary JARs are put into the unnamed module.
