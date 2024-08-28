---
lang: en-US
title: What is LuaJava?
description: LuaJava, a scripting tool for Java
---

# What is LuaJava?

[![Hello World Example](/hello.svg)](./examples/hello-world-mod.md)

[![Build Status](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml/badge.svg)](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Codecov](https://img.shields.io/codecov/c/github/gudzpoz/luajava?label=Coverage)](https://app.codecov.io/gh/gudzpoz/luajava/)
[![Java 8](https://img.shields.io/badge/Java-8-brown)](https://www.oracle.com/java/technologies/java8.html)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/party.iroiro.luajava/luajava?server=https%3A%2F%2Fs01.oss.sonatype.org&label=Nexus&color=pink)](https://s01.oss.sonatype.org/content/repositories/snapshots/party/iroiro/luajava/)
[![Maven Central](https://img.shields.io/maven-central/v/party.iroiro.luajava/luajava?color=blue&label=Maven%20Central)](https://mvnrepository.com/search?q=party.iroiro.luajava)

[![Build Status](https://github.com/gudzpoz/luajava/actions/workflows/docs.yml/badge.svg)](https://github.com/gudzpoz/luajava/actions/workflows/docs.yml)
[![Document Version](https://img.shields.io/github/package-json/v/gudzpoz/luajava?filename=docs%2Fpackage.json&label=Documentation)](https://gudzpoz.github.io/luajava/)

[![Tests: macOS on M1](https://img.shields.io/github/actions/workflow/status/gudzpoz/luajava/build-natives.yml?label=macOS%20on%20M1)](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml)
[![Tests: Linux on arm64](https://img.shields.io/circleci/build/github/gudzpoz/luajava/main?label=Linux%20on%20arm64)](https://app.circleci.com/pipelines/github/gudzpoz/luajava)

<style>
img + span svg.external-link-icon {
  opacity: 0;
}
</style>

This is yet another fork of [the original LuaJava](https://github.com/jasonsantos/luajava).

> LuaJava is a scripting tool for Java. The goal of this tool is to allow scripts written in Lua to manipulate components developed in Java. LuaJava allows Java components to be accessed from Lua using the same syntax that is used for accessing Lua's native objects, without any need for declarations or any kind of preprocessing.
>
> LuaJava also allows any Java interface to get implemented in Lua and passed as parameter to any method, and when called, the equivalent function will be called in Lua, the result passed back to Java.
>
> LuaJava is available under the same license as Lua 5.1, that is, it can be used at no cost for both academic and commercial purposes.

::: tip
Try it out with [a pre-built console](./console.md)!
:::

## Prerequisites

This library includes two sets of Java API:

1. A thin wrapper around the Lua C API.
   So you will need some basic understanding of the [Lua C API](https://www.lua.org/manual/5.1/manual.html#3).
   Most API names are renamed in CamelCases with the `lua_` or `luaL_` prefix removed.

   Additionally, if you are not satisfied with the API (which is an intersection between Lua 5.* versions),
   you can use the Lua C API bindings directly via [`Lua#getLuaNatives`](./javadoc/party/iroiro/luajava/Lua.html#getLuaNatives()){target="_self"}.

2. A more Java-ish API. This requires significantly less Lua knowledge. See [Java API](./java.md).

## Supported Lua Versions

Supported Lua versions are: Lua 5.1, Lua 5.2, Lua 5.3, Lua 5.4, LuaJ and LuaJIT.

I try to keep up with the most recent version of Lua, that is,
[the latest official release of Lua](https://www.lua.org/versions.html),
and the tip of [the v2.1 branch of LuaJIT](https://github.com/LuaJIT/LuaJIT/tree/v2.1) [^jit]
as well as the tip of a [LuaJ fork] that has no official release yet.
Currently:

<div style="display:flex;justify-content:center">

| Lua 5.1 | Lua 5.2 | Lua 5.3 | Lua 5.4 | LuaJIT      |    LuaJ     |
|:-------:|:-------:|:-------:|:-------:|:-----------:|:-----------:|
| 5.1.5   | 5.2.4   | 5.3.6   | 5.4.6   | [`ae4735f`] | [LuaJ fork] |

</div>

[`ae4735f`]: https://github.com/LuaJIT/LuaJIT/commits/ae4735f621d89d84758769b76432d2319dda9827

[LuaJ fork]: https://github.com/wagyourtail/luaj

[^jit]: LuaJIT no longer creates new releases. See [Project status · Issue #665](https://github.com/LuaJIT/LuaJIT/issues/665#issuecomment-784452583) for an explanation.

## Platforms

Thanks to [jnigen](https://github.com/libgdx/gdx-jnigen),
we have built Lua natives for almost all common platforms,
readily available on [Maven Central](https://mvnrepository.com/search?q=party.iroiro.luajava).
[^android] [^luaj]

<script setup>
const columns = ['Lua 5.1', 'Lua 5.2', 'Lua 5.3', 'Lua 5.4', 'LuaJIT', 'LuaJ'];
const android = 'Android <sup><a href="#fn2">[2]</a></sup>';
const matrix = {
  'Linux (x86_64)':   [2, 2, 2, 2, 2, 2],
  'Linux (x86)':      [1, 1, 1, 1, 1, 1],
  'Linux (ARM)':      [1, 1, 1, 1, 1, 1],
  'Linux (ARM64)':    [2, 2, 2, 2, 2, 2],
  'Windows (x86)':    [1, 1, 1, 1, 1, 1],
  'Windows (x86_64)': [2, 2, 2, 2, 2, 2],
  'MacOS (x86_64)':   [2, 2, 2, 2, 2, 2],
  'MacOS (ARM64)':    [2, 2, 2, 2, 2, 2],
  [android]:          [2, 2, 2, 2, 2, 2],
  'iOS':              [1, 1, 1, 1, 1, 1],
};
const classes = ['unsupported', 'available', 'tested'];
</script>

<style scoped>
div.legend {
  border: 1px solid var(--c-border-dark);
  display: inline-block;
  vertical-align: sub;
  width: 1em;
  height: 1em;
  margin-right: .3em;
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
ul {
  padding: 0;
}
ul li {
  display: inline-block;
  margin: .5em;
}
</style>

<ul>
  <li><div class="legend tested"></div>Available and tested</li>
  <li><div class="legend available"></div>Native available but not thoroughly tested yet</li>
  <li><div class="legend unsupported"></div>Not available (yet!)</li>
</ul>

<table class="matrix">
<tr><td></td><th v-for="col in columns" :key="col" v-text="col"></th></tr>
<tr v-for="(info, platform) in matrix" :key="platform">
  <th v-html="platform"></th>
  <td v-for="(support, i) in info" :key="columns[i]" :class="classes[support]" :alt="classes[support]"></td>
</tr>
</table>

[^android]: Android is available on many platforms, and we provide natives for `armeabi-v7a` `arm64-v8a` `x86` `x86_64`.
            It is tested against API levels 21, 24, 27, 30 and 33 on `x86_64` architectures
            (and on `x86` if a `default` emulator image is available) (and against my own phone of API level 30 on `arm64-v8a`).

[^luaj]: Since LuaJ is written in Java, it should theoretically run on all platforms that supports Java.
