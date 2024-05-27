# Java API

The Java API is mostly designed around
the [`party.iroiro.luajava.Lua`](../javadoc/party/iroiro/luajava/Lua.html) interface
and the [`party.iroiro.luajava.value.LuaValue`](../javadoc/party/iroiro/luajava/value/LuaValue.html) interface.

To start, simply acquire a `Lua` state with any of the following constructors:
- [`new Lua51()`](../javadoc/party/iroiro/luajava/lua51/Lua51.html)
- [`new Lua52()`](../javadoc/party/iroiro/luajava/lua52/Lua52.html)
- [`new Lua53()`](../javadoc/party/iroiro/luajava/lua53/Lua53.html)
- [`new Lua54()`](../javadoc/party/iroiro/luajava/lua54/Lua54.html)
- [`new LuaJit()`](../javadoc/party/iroiro/luajava/luajit/LuaJit.html)
- [`new LuaJ()`](../javadoc/party/iroiro/luajava/luaj/LuaJ.html)

## Close the state

Just like the C API, you will need to [`close`](../javadoc/party/iroiro/luajava/Lua.html#close()) the state after you are done with it:

<!-- @code:closableTest -->
@[code{100-107} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

::: warning
Currently the sub-threads (created with [`mainState.newThread()`](../javadoc/party/iroiro/luajava/Lua.html#newThread()))
are only cleaned up after you close the main state.
This shouldn't be a problem unless you have millions of sub-threads.
:::

## Open libraries

By default, the `Lua` state is only loaded with the [`java`](./api.md) library.
To use Lua libraries like `string`, `table` or `coroutine`, you will need to explicitly open the libraries:

- [openLibraries](../javadoc/party/iroiro/luajava/Lua.html#openLibraries()):
  Opens all available libraries.

- [openLibrary](../javadoc/party/iroiro/luajava/Lua.html#openLibrary(java.lang.String)):
  Opens a specific library.

## Interact with Lua values

To interact with Lua values, you can use the [`LuaValue`](#luavalue-interface) interface
or [make use of the Lua C API bindings directly](./c-api.md).

## `LuaValue` <Badge>interface</Badge>

The [`party.iroiro.luajava.value.LuaValue`](../javadoc/party/iroiro/luajava/value/LuaValue.html) interface
is a wrapper around a Lua value.
Internally, for complex values (for example, Lua tables),
it uses [references](#creating-references) to refer to the Lua value,
garbage collected after the `LuaValue` instance becomes a phantom reference.

<!-- @code:luaValueTest -->
@[code{17-23} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

[The JavaDoc of `LuaValue`](../javadoc/party/iroiro/luajava/value/LuaValue.html) should be quite self-explanatory.
The following texts list several commonly used patterns to help you get familiar with the API.

### Get global variables

Every `Lua` state implementation implements
the [`LuaThread`](../javadoc/party/iroiro/luajava/value/LuaThread.html) interface,
providing a convenient way to retrieve and use `LuaValues`:

To obtain a `LuaValue` from a Lua global variable,
use [`Lua::get(String)`](../javadoc/party/iroiro/luajava/value/LuaThread.html#get(java.lang.String)):

<!-- @code:luaValueFromGlobalTest -->
@[code{28-30} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### Set global variables

To set a Lua global variable to any `LuaValue` or any arbitrary Java object,
use [`Lua::set(String, Object)`](../javadoc/party/iroiro/luajava/value/LuaThread.html#set(java.lang.String,java.lang.Object)):

<!-- @code:setGlobalTest -->
@[code{35-44} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### `LuaValues` from Java values

To create a `LuaValue` from a simple Java value,
use [`Lua::from(boolean/double/long/String)`](../javadoc/party/iroiro/luajava/value/LuaThread.html#from(boolean))
or `Lua::fromNull()`.

<!-- @code:setGlobalTest -->
@[code{35-44} java{2}](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### `LuaValues` from Lua evaluation

To run Lua code and obtain the returned values,
use [`Lua::eval(String)`](../javadoc/party/iroiro/luajava/value/LuaThread.html#eval(java.lang.String)):

<!-- @code:luaValueEvalTest -->
@[code{49-55} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

::: tip
With `Lua::eval`, you will need an explicit Lua `return` statement to have `Lua::eval` return the values.
:::

### Manipulate Lua tables

`LuaValue`s implements the Java `Map` interface and allow direct manipulation of Lua tables.

<!-- @code:luaValueTableTest -->
@[code{60-73} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### Call Lua functions

Use [`LuaValue::call(...)`](../javadoc/party/iroiro/luajava/value/LuaValue.html#call(java.lang.Object...))
to call a function `LuaValue` and receive its return values as `LuaValue`s:

<!-- @code:luaValueCallTest -->
@[code{78-83} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)

### Create Java proxies

Create proxies with [`LuaValue::toProxy`](../javadoc/party/iroiro/luajava/value/LuaValue.html#toProxy(java.lang.Class)):

<!-- @code:luaValueProxyTest -->
@[code{88-94} java](../example/src/test/java/party/iroiro/luajava/docs/JavaApiExampleTest.java)
