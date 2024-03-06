# Development Guide

## Architecture

Now looking at this, I would say the code is rather scattered around the repository,
making the maintenance a bit painful even for me (who almost forgot where some of the code lies).
Hopefully this section will help those who are unfamiliar with the code base structure to get started.

### A Bit of History

This repository began as a fork of [Nonlua](https://github.com/deathbeam/jua) to publish the artifacts to Maven Central.
Later, I added some tests and found a bug that I had (and have) no clue about, which led me to a total rewrite.
Since Nonlua was LuaJIT-only, during the rewrite I decided to adapt to more Lua versions.
As I added more feature and Android builds, somehow, the repository ends up with a messed-up directory structure like this.

### Directory Structure

Most of the code lies in the following directories:

- **`jni/`** : Most of the JNI code that are common between all Lua versions supported.

- **`luajava/`** : Most of the Java code.

- **`lua51/`, `lua52/`, `lua53/`, `lua54/`, `luajit/`** : Lua version specific code (both JNI and Java code).

  - **`lua<version>/jni/lua<version>/`** : A git sub-module to the Lua source code repository.
  - **`lua<version>/jni/`** : Lua specific JNI code.
    (For example, `luaL_setmetatable` is not available in Lua 5.1, for which we manually implemented one.)

- **`jsr223/`** : An implementation of JSR 223.

- **`scripts/`** : Code generation scripts. Used to, e.g., generate Java native functions from Lua documentation.

Some other directories are used for testing and packaging.

- **`android/`** : Builds Android AAR artifacts.

- **`example/`** :

  This sub-project builds an `example-all.jar` as an experimental Lua REPL for users to test out LuaJava.
  Also:

  - **`example/docker/`** : Contains `Dockerfile`s used to set up some of the Linux testing environments.
  - **`example/suite/`** : Contains a test suite that is packaged into the `example-all.jar`,
    used to conveniently test in various environments.

Others:

- **`docs/`** : Documentation.
- **`jpms-example/`** : Ugh, just an example?

### Code Architecture

This repository uses [`jnigen`](https://github.com/libgdx/gdx-jnigen) to build JNI artifacts for multiple platforms.

- Java side:

  - The `party.iroiro.luajava` package in `luajava/` provides the Java API,
    relying on an abstract `LuaNative` class which will be implemented by artifacts of different Lua versions.

    - Methods in the `JuaAPI` class are mostly used from the JNI C++ side.

  - In `lua<version>/`, each artifact of a Lua version provides an implementation of `LuaNative`
    mostly by directly wrapping the corresponding Lua C API.

    The boilerplate JNI code is written right in the corresponding Java file
    (e.g., [`Lua51Natives.java`](./lua51/src/main/java/party/iroiro/luajava/lua51/Lua51Natives.java)),
    from which `jnigen` will extract the C code and generate a real C++ file used for compilation.

  - The `LuaNative` class contains the Lua C API common to all Lua versions.
    (Some are not, but they are manually implemented in `lua<version>/jni/mod/` instead.)

- JNI C++ side:

  - The "entry-point" is the `initBindings` function in `jni/luajava/jua.cpp`,
    which sets up variables used to interoperate with JVM.

  - When the Java side requests to set up a global Lua state, `initMetaRegistry` (also in `jua.cpp`) is run
    to set up necessary metatables.

## Setting Up

This section aims to walk you through the process needed to set the developing environment up.

You may also refer to the [GitHub building workflow](./.github/workflows/build-natives.yml)
for detailed instructions specific to GitHub workflow environments.

### Dependencies

This project cross-compiles Lua, which means it has quite a lot of dependencies (mostly compilation toolchains).
For a complete workflow to compile for all the platforms supported, see [build-natives.yml](./.github/workflows/build-natives.yml).

However, for development purposes, usually compiling for the current platform is enough,
and one does not need to install all the toolchains. Here we demonstrate how to set things up on Linux.
You will need to install at least the following:

- Android SDK

  I personally just install Android Studio and use their GUI to set things up.

- [Ant](https://ant.apache.org/)

  We use [`jnigen`](https://github.com/libgdx/gdx-jnigen/) to cross-compile binaries, which requires Ant.

- C/C++ compilation: `gcc g++ linux-libc-dev libc6-dev linux-libc-dev`.

  The package names may differ between Linux distributions.

- [Patched version](https://github.com/gudzpoz/patchelf/tree/verneed_fix) of [`patchelf`](https://github.com/NixOS/patchelf/)

  Symbol versioning in Glibc prevents the binaries from working between different Glibc versions,
  making some tricks necessary for LuaJIT cross-compilation.
  Here, we use `patchelf` to remove the versioning info altogether (see [`build.gradle`](./luajit/build.gradle)).

  However, the `--clear-symbol-verson` option in `patchelf` leaves the `.gnu.version_r` section behind,
  which still causes our symbol versioning problem. A PR has long been open but seems stale now.
  Anyway, you will need to compile that yourself.

  (For Arch Linux users, you may reuse `PKGBUILD` from `aur/patchelf-git`. Just remember to edit the git url,
  and checkout the `verneed_fix` branch.)

### Setup

If you are developing on an x86_64 machine, run the following commands to build the binaries:

```console
$ ./gradlew jnigen
$ ./gradlew jnigenBuildLinux64
```

You will need to manually recompile when you change any of the C/C++ files.
After compiling, things should work fine (including tests and bundling or whatsoever) and you can start developing.

### Testing

The tests lie in [`example/suite`](./example/suite) and [`example/src/test`](./example/src/test).
The former one is bundled into the example JAR to allow easier testing on different platforms.
However, to run the tests, try `./gradlew :example:test` or start tests in the latter from an IDE.
