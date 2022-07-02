# Getting Started

You may try things first [with an interactive console](./console.md).

## Getting It

::: tip
Go to [Initializer](#initializer) to get the required dependencies for your build system.
:::

The library consists of two parts:

1. The Java interface
2. The compiled native binaries

So you will need both to get LuaJava to work correctly. Basically, using Maven Central:

- The `groupId` is `party.iroiro.luajava`.
- The Java interface is `party.iroiro.luajava:luajava`.
- The Lua specific bridging artifacts are `lua5N` (`lua51` `lua52` ...) or `luajit`.
- The natives has `artifactId` like `lua5N-platform` (`lua51` `lua52` ...) or `luajit-platform`.

However, there are different native artifacts for different platforms, each with a different `classifier`:

- For desktop platforms, including Linux, Windows and MacOS, on x64 or x32 or ARM(32/64), we provide an integrated artifact with classifier `natives-desktop`.
- For mobile devices:
  - iOS: An artifact with classifier `natives-ios`.
  - Android: Since there are different architectures for Android, you can choose from the following four according to your target devices. (I recommend you to just include all the four though.)
    - Artifact with classifier `natives-armeabi-v7a`.
    - Artifact with classifier `natives-arm64-v8a`.
    - Artifact with classifier `natives-x86`.
    - Artifact with classifier `natives-x86_64`.

### Initializer

You may get the required dependencies for your build system using the following simple form.

<Matrix/>
