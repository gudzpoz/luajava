# Troubleshooting

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
