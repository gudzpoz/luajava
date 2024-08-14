# Using Binary Lua Libraries

On POSIX systems, JVM loads dynamical libraries with `dlopen`.
By default, it passes the `RTLD_LOCAL` flag to `dlopen`,
making the imported symbols only visible to JVM,
avoiding polluting the global symbol space (like namespace pollution).

However, if you are loading a binary Lua package, it will use some of the Lua C APIs,
which means it needs accessible to those imported symbols.
To make those symbols visible to our external libraries,
we will need to make them global by re-opening the library with a `RTLD_GLOBAL` flag.

```console
$ # Without the `RTLD_GLOBAL` flag
$ java -jar example/build/libs/example-all.jar --lua 5.1 -e 'require("lfs")'
java.lang.RuntimeException: error loading module 'lfs' from file './lfs.so':
	./lfs.so: undefined symbol: lua_gettop
$ # Re-opening with `RTLD_GLOBAL`
$ java -jar example/build/libs/example-all.jar --global --lua 5.1 -e 'require("lfs")'
```

However, this comes at a risk. If you ever tries to load two versions of Lua, say Lua 5.1 and Lua 5.2,
into JVM, with both of them global or one of them global, you will have no way to tell symbols from
one version to those from another.
JVM will very likely just crash on this.

We prevent loading multiple global Lua versions by some simple checks,
so you will probably see some exceptions instead of a VM crash.
But still, doing so is risky and will make your application hardly portable.

:::warning
Also, loading libraries as global is not tested on many platforms,
since I don't want to have to port the tests over every one of them.

You've been warned. But issues are welcome if you encounter any problems.
:::

To re-open the Lua library as global, you may do something like this:

<<< ../example/src/test/java/party/iroiro/luajava/docs/RtldExampleTest.java#loadAsGlobalTest

You only need to do this once per JVM, but it is safe to `loadAsGlobal` more than once.
