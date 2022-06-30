<template>
<div>
  <div><label v-for="(v, k) in managers"><input type="radio" :value="k" v-model="manager" name="manager"/>{{ k }}</label></div>
  <div><label v-for="(v, k) in luaVersions"><input type="radio" :value="k" v-model="lua" name="lua"/>{{ v }}</label></div>
  <div><label v-for="(v, k) in natives"><input type="radio" :value="k" v-model="platform" name="platform"/>{{ k }}</label></div>
  <pre :class="managerConfig[manager]"><code v-html="lines(lua, natives[platform], managers[manager])"></code></pre>
</div>
</template>

<script setup>
import { ref } from 'vue'
const groupId = 'party.iroiro.luajava'
const version = '3.0.0-SNAPSHOT'

const lua = ref('lua51')
const luaVersions = {
  lua51: 'Lua 5.1',
  lua52: 'Lua 5.2',
  lua53: 'Lua 5.3',
  lua54: 'Lua 5.4',
  luajit: 'LuaJIT',
}

const platform = ref('Desktop')
const natives = {
  Desktop: ['natives-desktop'],
  iOS: ['natives-ios'],
  Android: ['natives-armeabi-v7a', 'natives-arm64-v8a', 'natives-x86', 'natives-x86_64'],
}

function tag(tag, content) {
  return `<span class="token punctuation">&lt;</span><span class="token tag">${tag}</span><span class="token punctuation">&gt;</span>${content}<span class="token punctuation">&lt;/</span><span class="token tag">${tag}</span><span class="token punctuation">&gt;</span>`
}

const manager = ref('Gradle')
const managers = {
  Maven (groupId, artifactId, version, classifier) {
    return tag('dependency', Object.entries({ groupId, artifactId, version, classifier })
                                   .filter(e => e[1]).map(e => `\n    ${tag(e[0], e[1])}`)
                                   .join('') + '\n')
  },
  Gradle (groupId, artifactId, version, classifier) {
    return `implementation <span class="token string">${groupId}:${artifactId}:${version}${classifier ? ':' + classifier : ''}'</span>`
  },
}

const managerConfig = {
  Maven: 'language-xml',
  Gradle: 'language-groovy',
}

function lines(lua, natives, line) {
  return `${line(groupId, lua, version, null)}
${natives.map(n => line(groupId, lua + '-platform', version, n)).join('\n')}`
}
</script>

<style scoped>
label {
  display: inline-block;
  margin: .1em;
}
</style>