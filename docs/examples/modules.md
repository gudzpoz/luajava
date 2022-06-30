# Classpath Modules

With Java, in many cases you put your resources on the [classpath](https://en.wikipedia.org/wiki/Classpath), which may be a path accessible to Lua, or one lying in a [JAR](https://en.wikipedia.org/wiki/JAR_(file_format)).

Of course it is possible to load any Lua files manually by `Class::getResourceAsStream` and then reading it into a (direct) buffer and then `Lua::load(buffer, name)`.

Alternatively, we provide a `ClassPathLoader`. One just initializes the Lua state by setting a external loader, and then they can forget about the Java side classpath and just `require` any classpath Lua modules from Lua.

For more flexibility, just extend `ClassPathLoader` or write your own `ExternalLoader`.

:::: code-group
::: code-group-item Java Side
```java
Lua L = new Lua51();
L.openLibrary("package");
L.setExternalLoader(new ClassPathLoader());
```
:::
::: code-group-item Lua Side
```lua
-- Loads classpath:/lua/MyModule.lua
local MyModule = require('lua.MyModule')
```
:::
::::

