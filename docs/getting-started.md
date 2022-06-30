# Getting Started

You may try things first [with an interactive console](./console.md).

## Getting It

The library consists of two parts:

1. The Java interface
2. The compiled native binaries

So you will need both to get LuaJava to work correctly. For Gradle users, you may choose one of the following according to the wanted Lua version. For Maven users, check [#Maven](#maven).

### Gradle

<script setup>
const luaVersions = {
  lua51: 'Lua 5.1',
  lua52: 'Lua 5.2',
  lua53: 'Lua 5.3',
  lua54: 'Lua 5.4',
  luajit: 'LuaJIT',
}

const libraryVersion = '3.0.0'
</script>

<div v-for="(v, k) in luaVersions" :key="k">
<h4>{{ v }}</h4>
<pre class="language-groovy"><code>implementation <span class="token string">'party.iroiro.luajava:{{k}}:{{libraryVersion}}'</span>
implementation <span class="token string">'party.iroiro.luajava:{{k}}-platform:{{libraryVersion}}'</span></code></pre>
</div>

### Maven

<div v-for="(v, k) in luaVersions" :key="k">
<h4>{{ v }}</h4>
<pre class="language-xml"><code><span class="token punctuation">&lt;</span><span class="token tag">dependency</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">groupId</span><span class="token punctuation">&gt;</span>party.iroiro.luajava<span class="token punctuation">&lt;/</span><span class="token tag">groupId</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">artifactId</span><span class="token punctuation">&gt;</span>{{k}}<span class="token punctuation">&lt;/</span><span class="token tag">artifactId</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">version</span><span class="token punctuation">&gt;</span>{{libraryVersion}}<span class="token punctuation">&lt;/</span><span class="token tag">version</span><span class="token punctuation">&gt;</span>
<span class="token punctuation">&lt;/</span><span class="token tag">dependency</span><span class="token punctuation">&gt;</span>
<span class="token punctuation">&lt;</span><span class="token tag">dependency</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">groupId</span><span class="token punctuation">&gt;</span>party.iroiro.luajava<span class="token punctuation">&lt;/</span><span class="token tag">groupId</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">artifactId</span><span class="token punctuation">&gt;</span>{{k}}-platform<span class="token punctuation">&lt;/</span><span class="token tag">artifactId</span><span class="token punctuation">&gt;</span>
    <span class="token punctuation">&lt;</span><span class="token tag">version</span><span class="token punctuation">&gt;</span>{{libraryVersion}}<span class="token punctuation">&lt;/</span><span class="token tag">version</span><span class="token punctuation">&gt;</span>
<span class="token punctuation">&lt;/</span><span class="token tag">dependency</span><span class="token punctuation">&gt;</span>
</code></pre>
</div>
