import{_ as p,r as t,o as e,c as l,a as n,b as s,d as o,w as c,e as i}from"./app-BfXweHhW.js";const u={},d=n("h1",{id:"awt-example",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#awt-example"},[n("span",null,"AWT Example")])],-1),k={class:"custom-container tip"},r=n("p",{class:"custom-container-title"},"TIP",-1),v=i(`<div class="custom-container warning"><p class="custom-container-title">WARNING</p><p>Note that if you are using this in a normal Java application, you should take note of when you close the Lua state. You don&#39;t want to have the Lua state closed before the window is fully shutdown, which will definitely lead to problems.</p></div><div class="language-lua line-numbers-mode" data-ext="lua" data-title="lua"><pre class="language-lua"><code><span class="line"><span class="token comment">--[[</span>
<span class="line"></span>
<span class="line">Copyright (C) 2003-2007 Kepler Project.</span>
<span class="line"></span>
<span class="line">Permission is hereby granted, free of charge, to any person obtaining</span>
<span class="line">a copy of this software and associated documentation files (the</span>
<span class="line">&quot;Software&quot;), to deal in the Software without restriction, including</span>
<span class="line">without limitation the rights to use, copy, modify, merge, publish,</span>
<span class="line">distribute, sublicense, and/or sell copies of the Software, and to</span>
<span class="line">permit persons to whom the Software is furnished to do so, subject to</span>
<span class="line">the following conditions:</span>
<span class="line"></span>
<span class="line">The above copyright notice and this permission notice shall be</span>
<span class="line">included in all copies or substantial portions of the Software.</span>
<span class="line"></span>
<span class="line">THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND,</span>
<span class="line">EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF</span>
<span class="line">MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.</span>
<span class="line">IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY</span>
<span class="line">CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,</span>
<span class="line">TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE</span>
<span class="line">SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</span>
<span class="line"></span>
<span class="line">]]</span><span class="token comment">--</span></span>
<span class="line"></span>
<span class="line"><span class="token keyword">local</span> module <span class="token operator">=</span> <span class="token keyword">function</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> frame <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Frame&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Lua Java Console&quot;</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> console <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.TextArea&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> buttonsPanel <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Panel&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> executeButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Execute&quot;</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> clearButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Clear&quot;</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">local</span> exitButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Exit&quot;</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">setSize</span><span class="token punctuation">(</span><span class="token number">600</span><span class="token punctuation">,</span> <span class="token number">300</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>executeButton<span class="token punctuation">)</span></span>
<span class="line">  buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>clearButton<span class="token punctuation">)</span></span>
<span class="line">  buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>exitButton<span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  <span class="token keyword">local</span> BorderLayout <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.BorderLayout&quot;</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>BorderLayout<span class="token punctuation">.</span>NORTH<span class="token punctuation">,</span> console<span class="token punctuation">)</span></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>BorderLayout<span class="token punctuation">.</span>SOUTH<span class="token punctuation">,</span> buttonsPanel<span class="token punctuation">)</span></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">pack</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">show</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  <span class="token comment">--</span></span>
<span class="line">  <span class="token comment">-- Listeners</span></span>
<span class="line">  <span class="token comment">--</span></span>
<span class="line"></span>
<span class="line">  <span class="token keyword">local</span> luaload <span class="token operator">=</span> <span class="token keyword">nil</span></span>
<span class="line">  <span class="token keyword">if</span> loadstring <span class="token operator">~=</span> <span class="token keyword">nil</span> <span class="token keyword">then</span></span>
<span class="line">    luaload <span class="token operator">=</span> loadstring <span class="token comment">-- Lua 5.1</span></span>
<span class="line">  <span class="token keyword">else</span></span>
<span class="line">    luaload <span class="token operator">=</span> load <span class="token comment">-- Lua 5.2 and on</span></span>
<span class="line">  <span class="token keyword">end</span></span>
<span class="line"></span>
<span class="line">  executeButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span><span class="token keyword">function</span><span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;execute&quot;</span><span class="token punctuation">)</span></span>
<span class="line">    <span class="token function">pcall</span><span class="token punctuation">(</span><span class="token function">luaload</span><span class="token punctuation">(</span>console<span class="token punctuation">:</span><span class="token function">getText</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">end</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  clearButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span><span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;clear&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    console<span class="token punctuation">:</span><span class="token function">setText</span><span class="token punctuation">(</span><span class="token string">&quot;&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">  <span class="token keyword">end</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  exitButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span><span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;exit&quot;</span><span class="token punctuation">)</span></span>
<span class="line">    frame<span class="token punctuation">:</span><span class="token function">setVisible</span><span class="token punctuation">(</span><span class="token keyword">false</span><span class="token punctuation">)</span></span>
<span class="line">    frame<span class="token punctuation">:</span><span class="token function">dispose</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">end</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  <span class="token keyword">local</span> closeCallback <span class="token operator">=</span> <span class="token punctuation">{</span></span>
<span class="line">    windowClosing <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">      <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;close&quot;</span><span class="token punctuation">)</span></span>
<span class="line">      frame<span class="token punctuation">:</span><span class="token function">setVisible</span><span class="token punctuation">(</span><span class="token keyword">false</span><span class="token punctuation">)</span></span>
<span class="line">      frame<span class="token punctuation">:</span><span class="token function">dispose</span><span class="token punctuation">(</span><span class="token punctuation">)</span></span>
<span class="line">    <span class="token keyword">end</span><span class="token punctuation">,</span></span>
<span class="line">    windowIconified <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">      <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&#39;iconified&#39;</span><span class="token punctuation">)</span></span>
<span class="line">    <span class="token keyword">end</span></span>
<span class="line">  <span class="token punctuation">}</span></span>
<span class="line"></span>
<span class="line">  <span class="token function">setmetatable</span><span class="token punctuation">(</span>closeCallback<span class="token punctuation">,</span> <span class="token punctuation">{</span></span>
<span class="line">    __index <span class="token operator">=</span> <span class="token keyword">function</span><span class="token punctuation">(</span>t<span class="token punctuation">,</span> name<span class="token punctuation">)</span></span>
<span class="line">      <span class="token keyword">local</span> func <span class="token operator">=</span> <span class="token function">rawget</span><span class="token punctuation">(</span>t<span class="token punctuation">,</span> name<span class="token punctuation">)</span></span>
<span class="line">      <span class="token keyword">if</span> func <span class="token operator">~=</span> <span class="token keyword">nil</span> <span class="token keyword">then</span></span>
<span class="line">        <span class="token keyword">return</span> func</span>
<span class="line">      <span class="token keyword">else</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token keyword">function</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token function">print</span><span class="token punctuation">(</span>name<span class="token punctuation">)</span> <span class="token keyword">end</span></span>
<span class="line">      <span class="token keyword">end</span></span>
<span class="line">    <span class="token keyword">end</span></span>
<span class="line">  <span class="token punctuation">}</span><span class="token punctuation">)</span></span>
<span class="line"></span>
<span class="line">  <span class="token keyword">function</span> closeCallback<span class="token punctuation">.</span><span class="token function">windowActivated</span><span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span></span>
<span class="line">    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;act&quot;</span><span class="token punctuation">)</span></span>
<span class="line">  <span class="token keyword">end</span></span>
<span class="line"></span>
<span class="line">  frame<span class="token punctuation">:</span><span class="token function">addWindowListener</span><span class="token punctuation">(</span>closeCallback<span class="token punctuation">)</span></span>
<span class="line"><span class="token keyword">end</span></span>
<span class="line"></span>
<span class="line"><span class="token keyword">return</span> module</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,2);function m(b,f){const a=t("RouteLink");return e(),l("div",null,[d,n("div",k,[r,n("p",null,[s("With "),o(a,{to:"/console.html"},{default:c(()=>[s("Interactive Console")]),_:1}),s(", you may copy and paste the following Lua code to try it out.")])]),v])}const y=p(u,[["render",m],["__file","awt.html.vue"]]),h=JSON.parse('{"path":"/examples/awt.html","title":"AWT Example","lang":"en-US","frontmatter":{},"headers":[],"git":{"updatedTime":1719561160000},"filePathRelative":"examples/awt.md"}');export{y as comp,h as data};
