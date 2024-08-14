# LuaJava API Compatibility

The original LuaJava provides the following Lua API:

- `luajava.newInstance`
- `luajava.bindClass`
- `luajava.new`
- `luajava.createProxy`
- `luajava.loadLib`

By default, we do not offer these bindings. However, you may very easily adapt the `java` APIs to `luajava` ones.

<<< ../../example/suite/src/main/resources/suite/luajava-compat.lua
