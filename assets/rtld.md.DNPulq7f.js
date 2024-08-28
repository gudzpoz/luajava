import{_ as s,o as a,c as i,a5 as e}from"./chunks/framework.SYT71_kc.js";const b=JSON.parse('{"title":"Using Binary Lua Libraries","description":"","frontmatter":{},"headers":[],"relativePath":"rtld.md","filePath":"rtld.md","lastUpdated":1724853561000}'),n={name:"rtld.md"},l=e(`<h1 id="using-binary-lua-libraries" tabindex="-1">Using Binary Lua Libraries <a class="header-anchor" href="#using-binary-lua-libraries" aria-label="Permalink to &quot;Using Binary Lua Libraries&quot;">​</a></h1><p>On POSIX systems, JVM loads dynamical libraries with <code>dlopen</code>. By default, it passes the <code>RTLD_LOCAL</code> flag to <code>dlopen</code>, making the imported symbols only visible to JVM, avoiding polluting the global symbol space (like namespace pollution).</p><p>However, if you are loading a binary Lua package, it will use some of the Lua C APIs, which means it needs accessible to those imported symbols. To make those symbols visible to our external libraries, we will need to make them global by re-opening the library with a <code>RTLD_GLOBAL</code> flag.</p><div class="language-console vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">console</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">$ # Without the </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">\`</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">RTLD_GLOBAL</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">\`</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> flag</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">$ java -jar example/build/libs/example-all.jar --lua 5.1 -e </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&#39;require(&quot;lfs&quot;)&#39;</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">java.lang.RuntimeException: error loading module &#39;lfs&#39; from file &#39;./lfs.so&#39;:</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">	./lfs.so: undefined symbol: lua_gettop</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">$ # Re-opening with </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">\`</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">RTLD_GLOBAL</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">\`</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">$ java -jar example/build/libs/example-all.jar --global --lua 5.1 -e </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&#39;require(&quot;lfs&quot;)&#39;</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br><span class="line-number">6</span><br></div></div><p>However, this comes at a risk. If you ever tries to load two versions of Lua, say Lua 5.1 and Lua 5.2, into JVM, with both of them global or one of them global, you will have no way to tell symbols from one version to those from another. JVM will very likely just crash on this.</p><p>We prevent loading multiple global Lua versions by some simple checks, so you will probably see some exceptions instead of a VM crash. But still, doing so is risky and will make your application hardly portable.</p><div class="warning custom-block"><p class="custom-block-title">WARNING</p><p>Also, loading libraries as global is not tested on many platforms, since I don&#39;t want to have to port the tests over every one of them.</p><p>You&#39;ve been warned. But issues are welcome if you encounter any problems.</p></div><p>To re-open the Lua library as global, you may do something like this:</p><div class="language-java vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">try</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> (Lua L </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Lua54</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()) {</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">getLuaNatives</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">().</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">loadAsGlobal</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">();</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">run</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;require(&#39;lfs&#39;)&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br></div></div><p>You only need to do this once per JVM, but it is safe to <code>loadAsGlobal</code> more than once.</p>`,10),t=[l];function o(p,r,h,d,k,c){return a(),i("div",{"data-pagefind-body":!0},t)}const g=s(n,[["render",o]]);export{b as __pageData,g as default};
