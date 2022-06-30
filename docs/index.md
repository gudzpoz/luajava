---
lang: en-US
title: What is LuaJava?
description: LuaJava, a scripting tool for Java
---

# What is LuaJava?

This is yet another fork of [the original LuaJava](https://github.com/jasonsantos/luajava).

> LuaJava is a scripting tool for Java. The goal of this tool is to allow scripts written in Lua to manipulate components developed in Java. LuaJava allows Java components to be accessed from Lua using the same syntax that is used for accessing Lua's native objects, without any need for declarations or any kind of preprocessing.
>
> LuaJava also allows Java to implement an interface using Lua. This way any interface can be implemented in Lua and passed as parameter to any method, and when called, the equivalent function will be called in Lua, and it's result passed back to Java.
>
> LuaJava is available under the same license as Lua 5.1, that is, it can be used at no cost for both academic and commercial purposes.

## Supported Lua Versions

Supported Lua versions are: Lua 5.1, Lua 5.2, Lua 5.3, Lua 5.4 and LuaJIT.

I try to keep up with the most recent version of Lua, that is, [the latest official release of Lua](https://www.lua.org/versions.html), and the tip of [the v2.1 branch of LuaJIT](https://github.com/LuaJIT/LuaJIT/tree/v2.1).[^jit] Currently:

<div style="display:flex;justify-content:center">

| Lua 5.1 | Lua 5.2 | Lua 5.3 | Lua 5.4 |   LuaJIT    |
|:-------:|:-------:|:-------:|:-------:|:-----------:|
|  5.1.5  |  5.2.4  |  5.3.6  |  5.4.4  | [`4c2441c`] |

</div>

[`4c2441c`]: https://github.com/LuaJIT/LuaJIT/commits/4c2441c16ce3c4e312aaefecc6d40c4fe21de97c

[^jit]: LuaJIT no longer creates new releases. See [Project status · Issue #665](https://github.com/LuaJIT/LuaJIT/issues/665#issuecomment-784452583) for an explanation.

## Platforms

Thanks to [jnigen](https://github.com/libgdx/gdx-jnigen), we have built Lua natives for almost all common platforms, readily available on [Maven Central](https://mvnrepository.com/search?q=party.iroiro.luajava).

<script setup>
const columns = ['Lua 5.1', 'Lua 5.2', 'Lua 5.3', 'Lua 5.4', 'LuaJIT'];
const matrix = {
  'Linux(x64)':       [2, 2, 2, 2, 2],
  'Linux(x32)':       [1, 1, 1, 1, 1],
  'Linux(ARM)':       [1, 1, 1, 1, 1],
  'Linux(ARM64)':     [1, 1, 1, 1, 1],
  'Windows':          [1, 1, 1, 1, 1],
  'MacOS':            [2, 2, 2, 2, 2],
  'Android':          [1, 1, 1, 1, 1],
  'iOS':              [1, 1, 1, 1, 0],
};
const classes = ['unsupported', 'available', 'tested'];
</script>

<style>
div.legend {
  border: 1px solid var(--c-border-dark);
  display: inline-block;
  vertical-align: sub;
  width: 1em;
  height: 1em;
}
.tested {
  background-color: lightgreen;
}
.available {
  background-color: lightgoldenrodyellow;
}
.unsupported {
  background-color: lightgray;
}
.dark .tested {
  background-color: green;
}
.dark .available {
  background-color: darkkhaki;
}
.dark .unsupported {
  background-color: gray;
}
</style>

> <div class="legend tested"></div>
> Available and tested
> <div class="legend available"></div>
> Native available but not thoroughly tested yet
> <div class="legend unsupported"></div>
> Not available (yet!)

<table class="matrix">
<tr><td></td><th v-for="col in columns" :key="col" v-text="col"></th></tr>
<tr v-for="(info, platform) in matrix" :key="platform">
  <th v-text="platform"></th>
  <td v-for="(support, i) in info" :key="columns[i]" :class="classes[support]" :alt="classes[support]"></td>
</tr>
</table>
