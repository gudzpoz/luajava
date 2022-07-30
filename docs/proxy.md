# Proxy Caveats

Both [the Java API](./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class[],party.iroiro.luajava.Lua.Conversion)) and [the Lua API](./api.md#proxy-jclass-table-function) provide a way to create Java proxies that delegate calls to a underlying Lua table.

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
  run = function()
    print('Hello')
  end
})
```
:::
::::

However, there are a few thing that you might want to take note of.

::: tip TL;DR
1. Use `public` interfaces.
2. Currently, calling `default` functions is not supported on Android, and you will need to implement those methods yourself.
:::

## Access Levels

All interfaces implemented should be visible to all classes (i.e., `public`), although things might work for package-private interfaces depending on JVM security settings.

## Default Methods

### Illegal reflective access

Java 8 brings about default methods in interfaces.
However, the official reflection API is not adjusted accordingly,
making it a real pain to reflectively call the default methods from a proxy.

::: tip
If you are interested, there is an article on this: [Correct Reflective Access to Interface Default Methods in Java 8, 9, 10](https://blog.jooq.org/correct-reflective-access-to-interface-default-methods-in-java-8-9-10/). And you might want to check out the workarounds used by Spring: [DefaultMethodInvokingMethodInterceptor.java](https://github.com/spring-projects/spring-data-commons/blob/6a23723f07669e5d4031b3378b3af40e0d15eb82/src/main/java/org/springframework/data/projection/DefaultMethodInvokingMethodInterceptor.java).
:::

We use `unreflectSpecial` to find the default methods, which is only allowed if the caller is a subclass / subinterface. Since a proxy itself is not considered as a subclass of the implemented interfaces, you will very likely be seeing the following reflection warnings.

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access using Lookup on party.iroiro.luajava.LuaProxy (file:...) to interface ...
WARNING: Please consider reporting this to the maintainers of party.iroiro.luajava.LuaProxy
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

### Workaround

A way to work around this is to introduce some intermediate interfaces:

```
Original hierarchy:
  java.lang.Object
  |\
  | \- party.iroiro.luajava.LuaProxy -> has no access
   \
    \- the implemented interface1
    \- the implemented interface2

New hierarchy:
  java.lang.Object
  |\
  | \- party.iroiro.luajava.LuaProxy
   \
    \- interface1
    |   \- the actually implemented interface1 bridge -> has access
    |
    \- interface2
        \- the actually implemented interface2 bridge -> has access
```

We programmatically generate the intermediate interfaces, injecting some static methods
to look up the interfaces and let the final proxy object implement the intermediate interfaces instead.
By doing so, we finally obtain "legal reflective access".

::::: tip Try things out
The [interactive console](./console.md) bundles the ASM library with it. You don't need to enable the ASM workaround:

:::: code-group
::: code-group-item Lua Snippet
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
```
:::
::::

:::: code-group
::: code-group-item With the workaround
```shell-session
$ java --illegal-access=debug -jar example-all.jar
Lua Version: 5.1
Running Lua 5.1
>>> iterImpl = {
  >   next = function()
  >     i = i - 1
  >     return i
  >   end,
  >   hasNext = function()
  >     return i > 0
  >   end
  > }
  > 
  > iter = java.proxy('java.util.Iterator', iterImpl)
>>> iter:remove()
java.lang.UnsupportedOperationException: remove
```
:::
::: code-group-item Without
```shell-session
$ java -Dluajava_lookup=no --illegal-access=debug -jar example/build/libs/example-all.jar   
Lua Version: 5.1
Running Lua 5.1
>>> iterImpl = {
  >   next = function()
  >     i = i - 1
  >     return i
  >   end,
  >   hasNext = function()
  >     return i > 0
  >   end
  > }
  > 
  > iter = java.proxy('java.util.Iterator', iterImpl)
>>> iter:remove()
WARNING: Illegal reflective access using Lookup on party.iroiro.luajava.util.NastyLookupProvider (file:/tmp/example-all.jar) to interface java.util.Iterator
	at party.iroiro.luajava.util.NastyLookupProvider.lookup(NastyLookupProvider.java:72)
	at party.iroiro.luajava.util.ClassUtils.invokeDefault(ClassUtils.java:314)
	at party.iroiro.luajava.LuaProxy.callDefaultMethod(LuaProxy.java:77)
	at party.iroiro.luajava.LuaProxy.invoke(LuaProxy.java:39)
	at com.sun.proxy.$Proxy1.remove(Unknown Source)
	at party.iroiro.luajava.JuaAPI.methodInvoke(JuaAPI.java:546)
	at party.iroiro.luajava.JuaAPI.methodInvoke(JuaAPI.java:490)
	at party.iroiro.luajava.JuaAPI.objectInvoke(JuaAPI.java:203)
	at party.iroiro.luajava.Lua51Natives.luaL_dostring(Native Method)
	at party.iroiro.luajava.AbstractLua.run(AbstractLua.java:490)
	at party.iroiro.luajava.Console.startInteractive(Console.java:64)
	at party.iroiro.luajava.Console.main(Console.java:31)
java.lang.UnsupportedOperationException: remove
```
:::
::::

:::::

### Android

Android, on the other hand, does not allow loading `.class` data dynamically,
and thus the above approach is not yet viable. To add more to this, support for Java 8, let alone Java 9, is limited on Android and varies between API version...

In short, I cannot get it working on my own phone. Good luck to anyone trying to do so.

::: tip
We are trying to generate `.dex` data directly while minimizing external dependencies, which might take quite a while to finish though.
Feel free to contribute if you have better ideas!
:::
