# Troubleshooting

## Class (or Resource) Not Found

### Is a wrong classloader used?

By default, LuaJava tries the following classloaders for class loading,
and chooses the first non-null one:

1. `Thread.currentThread().getContextClassLoader()`
2. `party.iroiro.luajava.util.ClassUtils.class.getClassLoader()`
3. `ClassLoader.getSystemClassLoader()`

This might not be optimal if your class loading environment is not set up in this hierarchical way.
You may override `party.iroiro.luajava.util.ClassUtils#DEFAULT_CLASS_LOADER` to use a different class loader.
(It is not documented in the Javadoc, since it is quite internal and subject to changes.)

### Are you (mistakenly) using Java 9 modules?

If you package a fat JAR with, for example, [shadow](https://github.com/GradleUp/shadow),
please note that older versions of the plugins may not prune the `module-info.class` from some of your dependencies,
potentially making the whole JAR a large module.

It should be fine if the fat JAR is the only external JAR you load into the JVM.
But, if you plan to use this JAR as part of another application (e.g., as a plugin),
this can cause problems because the module system can restrict reflective access.
Try moving all `**/*/module-info.class` from your fat JAR.

## JVM Crashed

The crash is often followed by the following error message:

``` {2}
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x0000...
#
```

This is very likely a bug in this library. But if you are using the natives `Lua**Natives` directly, you might want to take note of the following:

### Have you pushed too many values onto the stack?

Lua imposes an initial stack size and an upper limit. You need to `lua_checkstack` (`Lua::checkStack(int extra)`)to allocate more stack slots.

When going beyond the current stack size, Lua just overwrites any data going after the stack without any notice, which will very likely result in all kinds of memory corruption.

We try to call `checkStack` for every stack incrementing operation. If we miss any, you are welcome to report it. But if you are using the `Lua**Natives` directly, you are on your own.

### A message like `FATAL ERROR in native method: ...`

It means Lua captured the error, but had no way to recover from it. For example:

```
FATAL ERROR in native method: error in __gc metamethod (stack overflow)
```

This might be caused by Lua's untimely GC. When you push a value right when the stack is nearly full (the maximum slots should be over several thousand), Lua might decide that it is time for garbage collection, which might call a `__gc` metamethod, which then overflows the stack.

Try to keep the number of items in stack lower than a thousand, and you will be safe.

### Have you mistaken the type of some element?

For example, if you try to `rawGetI` on a ***boolean***, ***number*** or any other type that is ***not a table***, the program *will* crash. We do not check the type for you, neither does Lua.
