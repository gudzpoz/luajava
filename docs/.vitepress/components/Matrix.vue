<template>
<div>
  <div><label v-for="(v, k) in managers"><input type="radio" :value="k" v-model="manager" name="manager"/>{{ k }}</label></div>
  <div><label v-for="(v, k) in luaVersions"><input type="radio" :value="k" v-model="lua" name="lua"/>{{ v }}</label></div>
  <div><label v-for="(v, k) in natives"><input type="radio" :value="k" v-model="platform" name="platform"/>{{ k }}</label></div>
  <div :class="managerConfig[manager]">
    <pre><code v-html="lines(lua, platform, managers[manager])"></code></pre>
  </div>
</div>
</template>

<script setup>
import { ref } from 'vue';
const groupId = 'party.iroiro.luajava';
const version = '4.0.2';

const notAvailable = {};

const lua = ref('lua51');
const luaVersions = {
  lua51: 'Lua 5.1',
  lua52: 'Lua 5.2',
  lua53: 'Lua 5.3',
  lua54: 'Lua 5.4',
  luajit: 'LuaJIT',
  luaj: 'LuaJ',
};

const platform = ref('Desktop');
const natives = {
  Desktop: ['natives-desktop'],
  iOS: ['natives-ios'],
};

function tag(tag, content) {
  return `<span class="token punctuation">&lt;</span><span class="token tag">${tag}</span><span class="token punctuation">&gt;</span>${content}<span class="token punctuation">&lt;/</span><span class="token tag">${tag}</span><span class="token punctuation">&gt;</span>`
}

const manager = ref('Gradle');
const managers = {
  Maven (groupId, artifactId, version, classifier, scope) {
    const entries = (scope === null
        ? { groupId, artifactId, version, classifier }
        : { groupId, artifactId, version, classifier, scope }
    );
    return tag('dependency', Object.entries(entries)
                                   .filter(e => e[1]).map(e => `\n    ${tag(e[0], e[1])}`)
                                   .join('') + '\n');
  },
  Gradle (groupId, artifactId, version, classifier, scope) {
    return `${ scope === 'runtime' ? 'runtimeOnly' : 'implementation'} <span class="token string">'${groupId}:${artifactId}:${version}${classifier ? ':' + classifier : ''}'</span>`
  },
};

const managerConfig = {
  Maven: 'language-xml',
  Gradle: 'language-groovy',
};

function lines(lua, platform, line) {
  if (notAvailable[lua] && notAvailable[lua][platform]) {
    return 'NOT AVAILABLE YET :(';
  } else {
    const native = natives[platform];
    return `${line(groupId, 'luajava', version, null)}
${line(groupId, lua, version, null)}
${lua === 'luaj' ? '' : native.map(n => line(groupId, lua + '-platform', version, n, 'runtime')).join('\n')}`;
  }
}
</script>

<style scoped>
label {
  display: inline-block;
  margin: .1em;
}
div.language-groovy pre code, div.language-xml pre code {
  color: var(--vp-c-warning-1);
}
pre :deep(.token.tag) {
  color: var(--vp-c-tip-1);
}
pre :deep(.token.punctuation) {
  color: var(--vp-c-text-2);
}
pre :deep(.token.string) {
  color: var(--vp-c-brand);
}
</style>
