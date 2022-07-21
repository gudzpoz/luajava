# Java API

The Java API is mostly a wrapper around [The Application Program Interface of Lua](https://www.lua.org/manual/5.1/manual.html).

## `LuaValue` <Badge>interface</Badge>

The [`party.iroiro.luajava.value.LuaValue`](../javadoc/party/iroiro/luajava/value/LuaValue.html) interface is a wrapper around a Lua value.

Internally, for complex values (for example, Lua tables), it uses [references](#creating-references) to refer to the Lua value. So if you have a long running Lua state, you should close the value to allow garbage collecting the referred value.

```java
Lua L = new Lua54();
LuaValue[] returnValues = L.execute("return { a = 1 }, 1024, 'string'");
assertEquals(3, returnValues.length);
assertEquals(L.from(1.0),      returnValues[0].get("a"));
assertEquals(L.from(1024),     returnValues[1]);
assertEquals(L.from("string"), returnValues[2]);
returnValues[0].close();
```

## `Lua` <Badge>interface</Badge>

The [`party.iroiro.luajava.Lua`](../javadoc/party/iroiro/luajava/Lua.html) interface is a wrapper around the `lua_State` pointer. It extracts the common parts from APIs of Lua 5.1 ~ 5.4 and LuaJIT. You may refer to the Javadoc for more information.

Some common patterns are listed below to help you get started.

### Closing the state

Just like the C API, you will need to [`close`](../javadoc/party/iroiro/luajava/Lua.html#close()) the state after you are done with it:

```java
Lua L = new Lua51();
// Operations
L.close();

// Or
try (Lua J = new Lua51()) {
  // Operations
}
```

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

```java {2,3}
Lua L = new Lua54();
L.push("string value");
L.setGlobal("myStr");

L.run("assert(myStr == 'string value')");
```

### Getting a global value

Similarly, [`getGlobal`](../javadoc/party/iroiro/luajava/Lua.html#getGlobal(java.lang.String))
pushes a value onto the stack, instead of returning it.

```java
L.run("a = 1024");
L.getGlobal("a");

L.toNumber(-1); // 1024.0 (double)
```

### Querying a table

We need to make use of
[`getField`](../javadoc/party/iroiro/luajava/Lua.html#getField(int,java.lang.String)),
[`getTable`](../javadoc/party/iroiro/luajava/Lua.html#getTable(int)),
[`rawGet`](../javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int))
or [`rawGetI`](../javadoc/party/iroiro/luajava/Lua.html#rawGetI(int,int)).

:::: code-group
::: code-group-item getField
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.getField(-1, "a");       // Retrieves the value
L.toNumber(-1);            // 1.0
```
:::
::: code-group-item rawGetI
```java
Lua L = new Lua54();
L.run("return { [20] = 1 }"); // Pushes a table on stack
L.rawGetI(-1, 20);            // Retrieves the value
L.toNumber(-1);               // 1.0
```
:::
::: code-group-item getTable
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.push("a");               // Pushes the key to look up
L.getTable(-2);            // Retrieves the value
L.toNumber(-1);            // 1.0
```
:::
::: code-group-item rawGet
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.push("a");               // Pushes the key to look up
L.rawGet(-2);              // Retrieves the value
L.toNumber(-1);            // 1.0
```
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
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.push(2);                 // Pushes the new value
L.setField(-2, "a");       // Updates the value
```
:::
::: code-group-item rawSetI
```java
Lua L = new Lua54();
L.run("return { [20] = 1 }"); // Pushes a table on stack
L.push(2);                    // Pushes the new value
L.rawSetI(-2, 20);            // Updates the value
```
:::
::: code-group-item setTable
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.push("a");               // Pushes the key
L.push(2);                 // Pushes the new value
L.getTable(-3);            // Updates the value
```
:::
::: code-group-item rawSet
```java
Lua L = new Lua54();
L.run("return { a = 1 }"); // Pushes a table on stack
L.push("a");               // Pushes the key
L.push(2);                 // Pushes the new value
L.rawSet(-3);              // Updates the value
```
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

