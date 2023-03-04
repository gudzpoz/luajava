import{_ as a,o as n,c as s,d as e}from"./app.d7e4dfcf.js";const t={},o=e(`<h1 id="luajava-api-compatibility" tabindex="-1"><a class="header-anchor" href="#luajava-api-compatibility" aria-hidden="true">#</a> LuaJava API Compatibility</h1><p>The original LuaJava provides the following Lua API:</p><ul><li><code>luajava.newInstance</code></li><li><code>luajava.bindClass</code></li><li><code>luajava.new</code></li><li><code>luajava.createProxy</code></li><li><code>luajava.loadLib</code></li></ul><p>By default, we do not offer these bindings. However, you may very easily adapt the <code>java</code> APIs to <code>luajava</code> ones.</p><div class="language-lua ext-lua line-numbers-mode"><pre class="language-lua"><code><span class="token keyword">local</span> luajava <span class="token operator">=</span> <span class="token punctuation">{</span>
  newInstance <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>className<span class="token punctuation">,</span> <span class="token punctuation">...</span><span class="token punctuation">)</span>
    <span class="token keyword">return</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span>className<span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">...</span><span class="token punctuation">)</span>
  <span class="token keyword">end</span><span class="token punctuation">,</span>

  bindClass <span class="token operator">=</span> java<span class="token punctuation">.</span>import<span class="token punctuation">,</span>

  new <span class="token operator">=</span> java<span class="token punctuation">.</span>new<span class="token punctuation">,</span>

  createProxy <span class="token operator">=</span> java<span class="token punctuation">.</span>proxy<span class="token punctuation">,</span>

  loadLib <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>className<span class="token punctuation">,</span> methodName<span class="token punctuation">)</span>
    <span class="token keyword">return</span> java<span class="token punctuation">.</span><span class="token function">loadlib</span><span class="token punctuation">(</span>className<span class="token punctuation">,</span> methodName<span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
  <span class="token keyword">end</span><span class="token punctuation">,</span>
<span class="token punctuation">}</span>

<span class="token keyword">return</span> luajava
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,5),p=[o];function c(l,i){return n(),s("div",null,p)}var d=a(t,[["render",c],["__file","compat.html.vue"]]);export{d as default};
