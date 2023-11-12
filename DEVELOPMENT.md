# Development Guide

This guide aims to walk you through the process needed to set the developing environment up.

## Dependencies

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

## Setup

If you are developing on an x86_64 machine, run the following commands to build the binaries:

```console
$ ./gradlew jnigen
$ ./gradlew jnigenBuildLinux64
```

You will need to manually recompile when you change any of the C/C++ files.
After compiling, things should work fine (including tests and bundling or whatsoever) and you can start developing.

## Testing

The tests lie in [`example/suite`](./example/suite) and [`example/src/test`](./example/src/test).
The former one is bundled into the example JAR to allow easier testing on different platforms.
However, to run the tests, try `./gradlew :example:test` or start tests in the latter from an IDE.
