# Debugging API

We don't provide `lua_Debug` or `lua_Hook` API bindings, because each Lua version
introduces some changes to the API (especially the `lua_Debug` structure) and
we can't cleanly support all of them.

Instead, all Lua versions already provide a debugging API with their `debug` library,
and we recommend the users using them instead.

::: tip
Because of our wrappers (in C or with `__call`), one might need to provide
a different level number to `debug.getinfo` to access the correct frame.
But otherwise, the usage generally follows the same pattern.
:::

Here is an example of using Java functions (`LuaFunction`) as hooks:

<<< ../../example/src/test/java/party/iroiro/luajava/docs/DebugExampleTest.java#debugCountTest{6-7,12-13}
