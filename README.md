# nonlua #
[![License](https://img.shields.io/github/license/nondev/nonlua.svg)](http://opensource.org/licenses/MIT)  [![Build Status](https://img.shields.io/travis/nondev/nonlua/master.svg)](https://travis-ci.org/nondev/nonlua)

  * [About](#about)
  * [Quickstart](#quickstart)
  * [Project template](#project-template)
  * [Credits](#credits)

## About ##

Nonlua is a scripting tool for Java. The goal of this tool is to allow scripts written in Lua to manipulate components developed in Java.

It allows Java components to be accessed from Lua using the same syntax that is used for accessing Lua`s native objects, without any need for declarations or any kind of preprocessing. Nonlua also allows Java to implement an interface using Lua. This way any interface can be implemented in Lua and passed as parameter to any method, and when called, the equivalent function will be called in Lua, and it's result passed back to Java.

## Quickstart ##

Here is simplest example on how to correctly initialize new Lua instance.
This example will push `message` variable to Lua with value `Hello World from Lua`, then prints it using Lua built-in `print` function and then tries to evaluate `main.lua` script.

```java
LuaConfiguration cfg = new LuaConfiguration();
Lua L = new Lua(cfg);

L.push("Hello World from Lua");
L.set("message");
L.run("print(message)");
L.run("main.lua");

L.dispose();
```

## Project template ##

To allow you to step into Lua development in Java, we build easy to use project template.
Just navigate into `template` directory and from there you can:
  
  1. Run it - `gradlew desktop:run`, `gradlew android:installDebug android:run`, `gradlew ios:launchIPhoneSimulator`
  2. Build it - `gradlew desktop:dist`, `gradlew android:assembleRelease`, `gradlew ios:createIPA`

## Credits ##

 * [LuaJava](https://github.com/jasonsantos/luajava)
 * [LibGDX](https://github.com/libgdx/libgdx)
 
Nonlua is based on LuaJava. So all thanks to Jason Santos for his awesome work on it.
Nonlua is also using gdx-jnigen for loading and compiling natives in cross-splatform way.