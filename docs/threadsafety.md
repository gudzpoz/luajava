# Thread Safety

::: tip TL;DR

For now:

<<< ../example/src/test/java/party/iroiro/luajava/docs/ThreadExampleTest.java#synchronizedTest

:::


No, we are not talking about Lua threads but OS threads.

The short answer is, **no**, we do not guarantee thread safety.
But you may safely access the Lua state across threads with a bit of external synchronization.

## Different main states

You do not need to worry if you use multiple Lua main states, each dedicated to one OS thread.

<<< ../example/src/test/java/party/iroiro/luajava/docs/ThreadExampleTest.java#differentStatesTest

## Same main state

<<< ../example/src/test/java/party/iroiro/luajava/docs/ThreadExampleTest.java#sameStateTest

From Java's perspective, there are two kinds of operations that might change the Lua state:

1. Directly manipulating the Lua state by calling any of the member methods of `party.iroiro.luajava.Lua`.
   (You never know what might trigger a Lua GC, so just assume all methods may change the state.)
2. Calling methods from the proxied objects, which will ultimately run the underlying Lua functions.

The key is that we synchronize on all these state-changing operations. Currently, the library synchronizes proxied calls with the main state:

```java ignored
public class LuaProxy implements InvocationHandler {

    public Object invoke(Object ignored, Method method, Object[] objects) {
        synchronized (L.getMainState()) {
            // Calling the underlying Lua function
        }
    }

}
```

If you doubt that the Lua state is to be accessed across threads,
then you are responsible to synchronize on the main state whenever you operate on any of the Lua threads.

::: warning
It is not a good API of course. But probably I will not change it in the nearly future.
:::
