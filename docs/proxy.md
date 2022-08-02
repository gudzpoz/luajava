# Proxy Caveats

Both [the Java API](./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class[],party.iroiro.luajava.Lua.Conversion))
and [the Lua API](./api.md#proxy-jclass-table-function) provide a way
to create Java proxies that delegate calls to an underlying Lua table.

The created Java object is passed as the first parameter to the member functions in Lua.
If you want to get the backing Lua table, use [`java.unwrap`](./api.md#unwrap-jobject-function).

:::: code-group
::: code-group-item Java API
```java
Lua L = new Lua54();
L.run("return { run = function() print('Hello') end }");
Runnable r = (Runnable) L.createProxy(new Class[] {Runnable.class}, Lua.Conversion.SEMI);
```
:::
::::

:::: code-group
::: code-group-item Lua API
```lua
r = java.proxy('java.lang.Runnable', {
  run = function(this)
    assert(type(java.unwrap(this)) == table)
    print('Hello')
  end
})
```
:::
::::

::: tip TL;DR
Things are finally working after all! Kudos to JNI.

Methods, if not implemented, will call the default methods in the interfaces.
We also provide a default implementation of `equals`, `hashCode` and `toString`.
:::

## Default Methods

Since Java 8, interfaces may choose to provide some `default` methods.

As is mentioned above, methods, if not implemented in the provided Lua table,
will call the default methods in the interfaces instead.

Moreover, we provide a way to explicitly calling the default methods in the interface.
See [`java.method`](#method-jobject-method-signature-function) for more information.

::: danger Security Concerns
This is dangerous because it provides a way to (somehow) work around the Java security manager.
This library does not sandbox Lua code currently and JNI itself can be risky.
Just don't execute unknown Lua code at all.

(Or you may open an issue on this if you consider sandboxing necessary.)
:::

::: tip Try things out
```lua
iterImpl = {
  next = function()
    i = i - 1
    return i
  end,
  hasNext = function()
    return i > 0
  end
}

iter = java.proxy('java.util.Iterator', iterImpl)

-- The default `remove` throws an UnsupportedOperationException
iter:remove()
-- Or explicitly calling `remove`
java.method(iter, 'java.util.Iterator:remove')()
```
:::
