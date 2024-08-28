import{_ as t,o as e,c as s,a5 as a}from"./chunks/framework.SYT71_kc.js";const u=JSON.parse('{"title":"Type Conversions","description":"","frontmatter":{},"headers":[],"relativePath":"conversions.md","filePath":"conversions.md","lastUpdated":1724853561000}'),i={name:"conversions.md"},n=a(`<h1 id="type-conversions" tabindex="-1">Type Conversions <a class="header-anchor" href="#type-conversions" aria-label="Permalink to &quot;Type Conversions&quot;">​</a></h1><p>Exchanging values between Java and Lua, we allow users to control how values are converted back and forth. You might want to read <a href="./api.html#extra-lua-types">#Extra Lua Types</a> first.</p><h2 id="java-to-lua" tabindex="-1">Java to Lua <a class="header-anchor" href="#java-to-lua" aria-label="Permalink to &quot;Java to Lua&quot;">​</a></h2><p>When pushing a Java value onto the Lua stack, you can choose from doing a <code>FULL</code> conversion, a <code>SEMI</code> conversion or <code>NONE</code> of the conversions by supplying a second parameter of type <code>Lua.Conversion</code>.</p><table tabindex="0"><thead><tr><th style="text-align:left;">Java Types</th><th style="text-align:center;"><a href="#lua-conversion-none"><code>NONE</code></a></th><th style="text-align:center;"><a href="#lua-conversion-semi"><code>SEMI</code></a></th><th style="text-align:center;"><a href="#lua-conversion-full"><code>FULL</code></a></th><th>Conversion (if ever)</th></tr></thead><tbody><tr><td style="text-align:left;"><code>null</code></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td>Lua <code>nil</code></td></tr><tr><td style="text-align:left;"><code>LuaValue</code></td><td style="text-align:center;">🟧</td><td style="text-align:center;">🟧</td><td style="text-align:center;">🟧</td><td>Converted if sharing main state</td></tr><tr><td style="text-align:left;"><code>Boolean</code></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td>Lua booleans</td></tr><tr><td style="text-align:left;"><code>Character</code></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td><code>lua_Integer</code> or <code>lua_Number</code></td></tr><tr><td style="text-align:left;">Boxed numerics (<code>Integer</code>...)</td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td><code>lua_Integer</code> or <code>lua_Number</code></td></tr><tr><td style="text-align:left;"><code>String</code></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td>Lua strings</td></tr><tr><td style="text-align:left;"><code>JFunction</code></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td style="text-align:center;">✅</td><td>A Lua closure</td></tr><tr><td style="text-align:left;">Java arrays</td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td>Lua tables (index starting from 1)</td></tr><tr><td style="text-align:left;"><code>Collections&lt;?&gt;</code></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td>Lua tables (index starting from 1)</td></tr><tr><td style="text-align:left;"><code>Map&lt;?, ?&gt;</code></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td>Lua tables</td></tr><tr><td style="text-align:left;"><code>Class&lt;?&gt;</code></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;">✅</td><td><code>jclass</code></td></tr><tr><td style="text-align:left;">Others</td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td>Proxied to the Java side</td></tr><tr><td style="text-align:left;">Example: <code>BigInteger</code></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td>Proxied to the Java side</td></tr><tr><td style="text-align:left;">Example: <code>AtomicInteger</code></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td style="text-align:center;"></td><td>Proxied to the Java side</td></tr></tbody></table><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>For a <code>SEMI</code>-conversion, roughly speaking, <strong>immutable</strong> types are converted, while mutable types, as well as those types not having Lua counterparts, are not.</p><p>For a <code>FULL</code>-conversion, all values are <strong>recursively</strong> converted if possible. Note that we ignore entries in <code>Map&lt;?&gt;</code> with a <code>null</code> key or a <code>null</code> value.</p><p>When calling Java methods from Lua, we <code>SEMI</code>-convert the return value. Currently, there is no way to specify how you want the return value converted.</p></div><div class="warning custom-block"><p class="custom-block-title">Examples</p><ul><li><p><strong><code>NONE</code></strong>:</p><p>You cannot add <code>jobject</code> types up, even if their underlying class is <code>Integer</code>.</p><div class="language-java vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">try</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> (Lua L </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Lua51</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()) {</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">push</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">1</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, Lua.Conversion.NONE);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">setGlobal</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;i&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">run</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;print(i:hashCode())&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line highlighted"><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">    assertThrows</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(LuaException.class, () </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">-&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">run</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;print(i + 1)&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">));</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">run</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;print(java.luaify(i) + 1)&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br><span class="line-number">6</span><br><span class="line-number">7</span><br></div></div></li><li><p><strong><code>FULL</code></strong>:</p><p>Changes in converted Lua objects are not propagated back to the original Java object.</p><div class="language-java vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">try</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> (Lua L </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Lua51</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()) {</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    int</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">[] array </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> int</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">[]{</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">100</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">};</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">push</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(array, Lua.Conversion.FULL);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">setGlobal</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;array&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line highlighted"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">run</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;array[1] = 1024&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line highlighted"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    assert</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;"> 100</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> ==</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> array[</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">0</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">];</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br><span class="line-number">6</span><br><span class="line-number">7</span><br></div></div></li></ul></div><h2 id="lua-to-java" tabindex="-1">Lua to Java <a class="header-anchor" href="#lua-to-java" aria-label="Permalink to &quot;Lua to Java&quot;">​</a></h2><ol><li><p><em><strong>nil</strong></em> is converted to <code>null</code>.</p></li><li><p><em><strong>boolean</strong></em> converted to <code>boolean</code> or the boxed <code>Boolean</code>.</p></li><li><p><em><strong>integer</strong></em> / <em><strong>number</strong></em> to any of <code>boolean</code> <code>char</code> <code>byte</code> <code>short</code> <code>int</code> <code>long</code> <code>float</code> <code>double</code> or their boxed alternative. (<code>Double</code> is preferred.)</p><p>Trying to convert a number into an <code>Object</code> will always yield a boxed <code>Double</code>. So pay attention when you use <code>Object::equals</code> for example.</p></li><li><p><em><strong>string</strong></em> to <code>String</code>.</p></li><li><p><em><strong>table</strong></em> to <code>Map&lt;Object, Object&gt;</code>, <code>List&lt;Object&amp;gt;</code>, <code>Object[]</code>, (converted recursively with <code>Map&lt;Object, Object&gt;</code> preferred) or any interfaces.</p><p>To convert tables into any interface, we call <a href="./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class%5B%5D,party.iroiro.luajava.Lua.Conversion)" target="_self"><code>party.iroiro.luajava.Lua#createProxy</code></a> with <a href="./javadoc/party/iroiro/luajava/Lua.Conversion.html#SEMI" target="_self"><code>party.iroiro.luajava.Lua.Conversion#SEMI</code></a>.</p></li><li><p><em><strong>jclass</strong></em> to <code>Class&lt;?&gt;</code>.</p></li><li><p><em><strong>jobject</strong></em> to the underlying Java object.</p></li><li><p><em><strong>function</strong></em> to a <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.8" target="_blank" rel="noreferrer">functional interface</a>.</p><p>Actually, if the abstract methods in the target interfaces are all of the same name, the library also does the wrapping for you.</p><p>The wrapping is done by creating an intermediate Lua table and then calling <a href="./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class%5B%5D,party.iroiro.luajava.Lua.Conversion)" target="_self"><code>party.iroiro.luajava.Lua#createProxy</code></a> with <a href="./javadoc/party/iroiro/luajava/Lua.Conversion.html#SEMI" target="_self"><code>party.iroiro.luajava.Lua.Conversion#SEMI</code></a>.</p></li><li><p>Any type can get wrapped into a <code>LuaValue</code>.</p></li><li><p>If all the above is not applicable, the result is <code>null</code> on the Java side.</p></li></ol><div class="warning custom-block"><p class="custom-block-title">WARNING</p><p>Currently, you cannot convert a C closure back to a <code>JFunction</code>, even if the closure simply wraps around <code>JFunction</code>.</p></div><h2 id="_64-bit-integers" tabindex="-1">64-Bit Integers <a class="header-anchor" href="#_64-bit-integers" aria-label="Permalink to &quot;64-Bit Integers&quot;">​</a></h2><p>To ensure compatibility across Lua versions, this library uses <code>double</code> for most numbers. However, <a href="https://www.lua.org/manual/5.3/manual.html#8.1" target="_blank" rel="noreferrer">Lua 5.3</a> introduced an integer subtype for numbers, which allows usage of 64-bit integers in Lua (on 64-bit machines mostly). This library ensures that no truncation ever happens when casting between <code>long</code> and <code>double</code> (which can happen on 32-bit machines where <code>long</code> values get truncated to 32-bit Lua integers). To retrieve or push integer values that exceed the safe integer range of <code>double</code> numbers, you will need to use <a href="./javadoc/party/iroiro/luajava/Lua.html#push(long)" target="_self"><code>party.iroiro.luajava.Lua#push(long)</code></a> and <a href="./javadoc/party/iroiro/luajava/Lua.html#toInteger(int)" target="_self"><code>party.iroiro.luajava.Lua#toInteger</code></a>.</p><p>Also, when passing values via proxies or Java calls on the Lua side, the values will get auto converted to ensure maximal precision. For example, the following Lua snippet passes <code>2^60 + 1</code> around correctly (which cannot fit into a <code>double</code>) when running with 64-bit Lua 5.3:</p><div class="language-lua vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">lua</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">POW_2_60 </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;"> 1152921504606846976</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">Long </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> java.</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">import</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&#39;java.lang.Long&#39;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">l </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;"> Long</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(POW_2_60 </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">+</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;"> 1</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">assert</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">l</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">:</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">toString</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">==</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;"> &quot;1152921504606846977&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">assert</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">l</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">:</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">longValue</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">==</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;"> 1152921504606846977</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br></div></div>`,14),l=[n];function r(o,d,h,p,c,k){return e(),s("div",{"data-pagefind-body":!0},l)}const y=t(i,[["render",r]]);export{u as __pageData,y as default};
