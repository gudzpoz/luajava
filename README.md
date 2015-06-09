# nonlua #
[![Build Status](https://travis-ci.org/nondev/nonlua.svg?branch=master)](https://travis-ci.org/nondev/nonlua)

  * [Supported platforms](#supported-platforms)
  * [About](#about)
  * [Project template](#project-template)
  * [Credits](#credits)

## Supported platforms ##

  * Windows
  * Linux
  * Mac OS X
  * Android (and Ouya)
  * iOS

## About ##

nonlua is a scripting tool for Java. The goal of this tool is to allow scripts written in Lua to manipulate components developed in Java.

It allows Java components to be accessed from Lua using the same syntax that is used for accessing Lua`s native objects, without any need for declarations or any kind of preprocessing. nonlua also allows Java to implement an interface using Lua. This way any interface can be implemented in Lua and passed as parameter to any method, and when called, the equivalent function will be called in Lua, and it's result passed back to Java.

## Project template ##

To allow you to step into Lua development in Java, we build easy to use project template.
Just navigate into `template` directory and from there you can:
  
  1. Run it - `gradlew desktop:run`, `gradlew android:installDebug android:run`, `gradlew ios:launchIPhoneSimulator`
  2. Build it - `gradlew desktop:dist`, `gradlew android:assembleRelease`, `gradlew ios:createIPA`

## Credits ##

 * [LuaJava](https://github.com/jasonsantos/luajava)
 * [LibGDX](https://github.com/libgdx/libgdx)
 
nonlua is based on LuaJava. So all thanks to Jason Santos for his awesome work on it.
nonlua is also for cross-platform compatibility using gdx-jnigen for loading and compiling natives in cross-platform way.