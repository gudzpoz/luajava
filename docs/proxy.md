# Proxy Caveats

::: warning
The following won't work when using LuaJ bindings.
:::

Both [the Java API](./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class[],party.iroiro.luajava.Lua.Conversion)){target="_self"}
and [the Lua API](./api.md#proxy-jclass-table-function) provide a way
to create Java proxies that delegate calls to an underlying Lua table.

The created Java object is passed as the first parameter to the member functions in Lua.
If you want to get the backing Lua table, use [`java.unwrap`](./api.md#unwrap-jobject-function).

::: code-group

<<< ../example/src/test/java/party/iroiro/luajava/docs/ProxyExampleTest.java#runnableTest [Java API]

:::

::: code-group

<<< ../example/src/test/resources/docs/proxyExampleTest.lua [Lua API]

:::

::: tip TL;DR
Things are finally working after all! Kudos to JNI.

Methods, if not implemented, will call the default methods in the interfaces.
We also provide a default implementation of `equals`, `hashCode` and `toString`.
:::

## Default Methods

Since Java 8, interfaces may choose to provide some `default` methods.

As is mentioned above, methods, if not implemented in the provided Lua table,
will call the default methods in the interfaces instead.

Moreover, we provide a way to explicitly call the default methods in the interface.
See [`java.method`](#method-jobject-method-signature-function) for more information.

::: danger Security Concerns
This is dangerous because it provides a way to (somehow) work around the Java security manager.
This library does not sandbox Lua code currently, and JNI itself can be risky.
Just don't execute unknown Lua code at all.

(Or you may open an issue on this if you consider sandboxing necessary.)
:::

::: details A Lua snippet to try things out
<<< ../example/src/test/resources/docs/apiMethodExample3.lua
:::

### And Android...

::: tip TL;DR
1. If you are creating a proxy implementing your own interfaces (i.e. not provided by Java runtime),
    you need a `minSdkVersion` of `24` or higher.
2. If you are implementing interfaces provided by the Java runtime, it depends on the actual API level of the device that it runs on.
:::

Default methods require API level 24 (Android 7.0).
If your Lua code depend on some default methods,
it not only means that the code will fail on API levels below,
but also it will fail for *all* default methods implemented with [Desugaring].

[Desugaring]: https://developer.android.com/studio/write/java8-support

I am not going to detail on this. In short, if you have `minSdkVersion` lower then `24`,
your interfaces with default methods will get transformed into a normal interface
*and* an abstract class containing the implementation:

```java ignored
// Written code:
interface DefaultedInterface { default int answer() { return 42; } }
// Transformed:
interface DefaultedInterface { int answer(); }
abstract class GeneratedAbstractBlahBlah implements DefaultedInterface {
    int answer() { return 42; }
}
```

So any `class Impl implements DefaultedInterface` is actually `class Impl extends GeneratedAbstractBlahBlah`.
Our proxy only implements the `DefaultedInterface`, which has no default methods at all after desugaring, and knows nothing about `GeneratedAbstractBlahBlah`. So it *will* fail.

## Threads

Calling `Lua#createProxy` actually means that the target function will be executed on that thread,
on whose stack all the parameter conversions happen.

Don't close a thread with proxies still in use.
