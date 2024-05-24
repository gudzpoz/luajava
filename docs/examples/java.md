# Java API

The Java API is mostly a wrapper around [The Application Program Interface of Lua](https://www.lua.org/manual/5.1/manual.html).

## `LuaValue` <Badge>interface</Badge>

The [`party.iroiro.luajava.value.LuaValue`](../javadoc/party/iroiro/luajava/value/LuaValue.html) interface is a wrapper around a Lua value.

<!-- @code:luaValueTest -->
@[code{16-22} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

Internally, for complex values (for example, Lua tables), it uses [references](#creating-references) to refer to the Lua value.

### `LuaValue` interface features

- A `Map` implementation to allow direct manipulation of Lua tables.
- Garbage collected references to avoid memory leaks on the Lua side.
- Proxy creation with [`LuaValue::toProxy`](../javadoc/party/iroiro/luajava/value/LuaValue.html#toProxy(java.lang.Class))

### Obtaining a `LuaValue`

- [`Lua::get(java.lang.String)`](../javadoc/party/iroiro/luajava/Lua.html#get(java.lang.String)):
  Returns a global variable of the supplied name as a `LuaValue`
- [`Lua::eval(java.lang.String)`](../javadoc/party/iroiro/luajava/Lua.html#eval(java.lang.String)):
  Executes the supplied Lua code and returns the returned value or values.

  ::: tip
  With `Lua::eval`, you will need an explicit Lua `return` statement to have `Lua::eval` return the values.

  <!-- @code:luaValueEvalTest -->
  @[code{27-33} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)
  :::

## `Lua` <Badge>interface</Badge>

The [`party.iroiro.luajava.Lua`](../javadoc/party/iroiro/luajava/Lua.html) interface is a wrapper around the `lua_State` pointer. It extracts the common parts from APIs of Lua 5.1 ~ 5.4 and LuaJIT. You may refer to the Javadoc for more information.

Some common patterns are listed below to help you get started.

### Closing the state

Just like the C API, you will need to [`close`](../javadoc/party/iroiro/luajava/Lua.html#close()) the state after you are done with it:

<!-- @code:closableTest -->
@[code{39-46} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

::: warning
Currently the sub-threads (created with [`mainState.newThread()`](../javadoc/party/iroiro/luajava/Lua.html#newThread()))
are only cleaned up after you close the main state.
This shouldn't be a problem unless you have millions of sub-threads.
:::

### Open libraries

You need to explicitly open the libraries you use.

- [openLibraries](../javadoc/party/iroiro/luajava/Lua.html#openLibraries()):
  Opens all available libraries.

- [openLibrary](../javadoc/party/iroiro/luajava/Lua.html#openLibrary(java.lang.String)):
  Opens a specific library.

### Setting a global value

Lua API bases on a Lua stack. You need to push the value onto the stack before assigning it
to a global value with [`setGlobal`](../javadoc/party/iroiro/luajava/Lua.html#setGlobal(java.lang.String)).

<!-- @code:globalSetTest -->
@[code{51-59} java{6-7}](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### Getting a global value

Similarly, [`getGlobal`](../javadoc/party/iroiro/luajava/Lua.html#getGlobal(java.lang.String))
pushes a value onto the stack, instead of returning it.

<!-- @code:globalGetTest -->
@[code{64-71} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### Querying a table

We need to make use of
[`getField`](../javadoc/party/iroiro/luajava/Lua.html#getField(int,java.lang.String)),
[`getTable`](../javadoc/party/iroiro/luajava/Lua.html#getTable(int)),
[`rawGet`](../javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int))
or [`rawGetI`](../javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int)).

:::: code-group
::: code-group-item getField

<!-- @code:getFieldTest -->
@[code{76-80} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item rawGetI

<!-- @code:rawGetITest -->
@[code{85-89} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item getTable

<!-- @code:getTableTest -->
@[code{94-99} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item rawGet

<!-- @code:rawGetTest -->
@[code{104-109} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::::

### Updating a table

Similarly, we have
[`setField`](../javadoc/party/iroiro/luajava/Lua.html#setField(int,java.lang.String))
[`setTable`](../javadoc/party/iroiro/luajava/Lua.html#setTable(int))
[`rawSet`](../javadoc/party/iroiro/luajava/Lua.html#rawSet(int))
and [`rawSetI`](../javadoc/party/iroiro/luajava/Lua.html#rawSetI(int,int)).

:::: code-group
::: code-group-item setField

<!-- @code:setFieldTest -->
@[code{114-118} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item rawSetI

<!-- @code:rawSetITest -->
@[code{123-127} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item setTable

<!-- @code:setTableTest -->
@[code{132-137} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item rawSet

<!-- @code:rawSetTest -->
@[code{142-147} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::::

### Creating references

A reference is a unique integer key: `table[reference] = referredValue`. Lua provides a convenient way to store values into a table, returning the generated reference key.

See
[`ref()`](../javadoc/party/iroiro/luajava/Lua.html#ref())
[`ref(int)`](../javadoc/party/iroiro/luajava/Lua.html#ref(int))
[`refGet(int)`](../javadoc/party/iroiro/luajava/Lua.html#refGet(int))
[`unref(int)`](../javadoc/party/iroiro/luajava/Lua.html#unref(int))
and [`unRef(int, int)`](../javadoc/party/iroiro/luajava/Lua.html#unRef(int,int)) for more info.

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

:::: code-group
::: code-group-item Use lua_dump

<!-- @code:luaDumpTest -->
@[code{160-167} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::: code-group-item Use `string.dump`

<!-- @code:stringDumpTest -->
@[code{172-179} java](../../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

:::
::::

Dumping functions involves some more Lua knowledge such as up-values and environments.
What these terms mean might differ between versions and is not a topic of this document.
