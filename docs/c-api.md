# Lua Stack-based C API

The [`party.iroiro.luajava.Lua`](./javadoc/party/iroiro/luajava/Lua.html){target="_self"} interface
is a wrapper around the `lua_State` pointer.
Besides [`LuaValue`-relevant methods](./java.md),
it also provides the common parts from the C APIs of Lua 5.1 ~ 5.4 and LuaJIT.
(You are recommended to use [the `LuaValue` API](./java.md) though.)
You may refer to the Javadoc for more information.

## `Lua` <Badge>interface</Badge>

The Java API is mostly a wrapper around
[The Application Program Interface of Lua](https://www.lua.org/manual/5.1/manual.html).
Some common patterns are listed below to help you get started.

### Setting a global value

Lua API bases on a Lua stack. You need to push the value onto the stack before assigning it
to a global value with [`setGlobal`](./javadoc/party/iroiro/luajava/Lua.html#setGlobal(java.lang.String)){target="_self"}.

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#globalSetTest{6-7}

### Getting a global value

Similarly, [`getGlobal`](./javadoc/party/iroiro/luajava/Lua.html#getGlobal(java.lang.String)){target="_self"}
pushes a value onto the stack, instead of returning it.

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#globalGetTest

### Querying a table

We need to make use of
[`getField`](./javadoc/party/iroiro/luajava/Lua.html#getField(int,java.lang.String)){target="_self"},
[`getTable`](./javadoc/party/iroiro/luajava/Lua.html#getTable(int)){target="_self"},
[`rawGet`](./javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int)){target="_self"}
or [`rawGetI`](./javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int)){target="_self"}.

::: code-group

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#getFieldTest [getField]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#rawGetITest [rawGetI]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#getTableTest [getTable]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#rawGetTest [rawGet]

:::

### Updating a table

Similarly, we have
[`setField`](./javadoc/party/iroiro/luajava/Lua.html#setField(int,java.lang.String)){target="_self"}
[`setTable`](./javadoc/party/iroiro/luajava/Lua.html#setTable(int)){target="_self"}
[`rawSet`](./javadoc/party/iroiro/luajava/Lua.html#rawSet(int)){target="_self"}
and [`rawSetI`](./javadoc/party/iroiro/luajava/Lua.html#rawSetI(int,int)){target="_self"}.

::: code-group

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#setFieldTest [setField]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#rawSetITest [rawSetI]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#setTableTest [setTable]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#rawSetTest [rawSet]

:::

### Creating references

A reference is a unique integer key: `table[reference] = referredValue`. Lua provides a convenient way to store values into a table, returning the generated reference key.

See
[`ref()`](./javadoc/party/iroiro/luajava/Lua.html#ref()){target="_self"}
[`ref(int)`](./javadoc/party/iroiro/luajava/Lua.html#ref(int)){target="_self"}
[`refGet(int)`](./javadoc/party/iroiro/luajava/Lua.html#refGet(int)){target="_self"}
[`unref(int)`](./javadoc/party/iroiro/luajava/Lua.html#unref(int)){target="_self"}
and [`unRef(int, int)`](./javadoc/party/iroiro/luajava/Lua.html#unRef(int,int)){target="_self"} for more info.

### Pre-compiled chunks

According to the Lua reference manual:

> Chunks can also be pre-compiled into binary form;
> see program luac for details.
> Programs in source and compiled forms are interchangeable;
> Lua automatically detects the file type and acts accordingly.

Replacing Lua source files with pre-compiled chunks speeds up loading.
However, according to `luac` manual:

> Precompiled chunks are *not* portable across different architectures.
> Moreover, the internal format of precompiled chunks is likely to change
> when a new version of Lua is released.  Make sure you save  the  source
> files of all Lua programs that you precompile.

Binary chunks compiled on one platform may not run on another.
Since we are using Java / JVM-based languages, this is absolutely not desirable.
To work around this, you may either:
1. Provide precompiled chunks for all your target platforms;
2. Or compile them at runtime for once and just reuse the compiled binaries.

Here is a tiny example for the second approach:

::: code-group

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#luaDumpTest [Use lua_dump]

<<< ../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java#stringDumpTest [Use `string.dump`]

:::

Dumping functions involves some more Lua knowledge such as up-values and environments.
What these terms mean might differ between versions and is not a topic of this document.

## `LuaNatives` <Badge>interface</Badge>

The [`LuaNatives`](./javadoc/party/iroiro/luajava/LuaNatives.html){target="_self"} interface
exposes common JNI bindings of the C API of Lua 5.1 ~ Lua 5.4.
Use with caution.

- Get an instance of `LuaValue` with [`LuaValue::getLuaNatives()`](./javadoc/party/iroiro/luajava/Lua.html#getLuaNatives()){target="_self"}.
- Get the pointer to a `Lua` state with [`Lua::getPointer()`](./javadoc/party/iroiro/luajava/Lua.html#getPointer()){target="_self"}.

If you want to use C API functions specific to a Lua version,
simply cast them to the corresponding `LuaNatives` implementation:

- [`Lua51Natives`](./javadoc/party/iroiro/luajava/lua51/Lua51Natives.html){target="_self"}
- [`Lua52Natives`](./javadoc/party/iroiro/luajava/lua52/Lua52Natives.html){target="_self"}
- [`Lua53Natives`](./javadoc/party/iroiro/luajava/lua53/Lua53Natives.html){target="_self"}
- [`Lua54Natives`](./javadoc/party/iroiro/luajava/lua54/Lua54Natives.html){target="_self"}
- [`LuaJitNatives`](./javadoc/party/iroiro/luajava/luajit/LuaJitNatives.html){target="_self"}
- [`LuaJNatives`](./javadoc/party/iroiro/luajava/luaj/LuaJNatives.html){target="_self"}

::: tip
`LuaJNatives`, which uses LuaJ, a Lua implementation in Java,
is actually implemented in Java to provide a consistent API.
It should not differ too much from other implementations though.
:::
