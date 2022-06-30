import{_ as t,r as p,o,c as e,a as n,b as c,w as i,e as s,d as u}from"./app.b9a4d796.js";const l={},k=n("h1",{id:"awt-example",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#awt-example","aria-hidden":"true"},"#"),s(" AWT Example")],-1),r={class:"custom-container tip"},d=n("p",{class:"custom-container-title"},"TIP",-1),v=s("With "),m=s("Interactive Console"),b=s(", you may copy and paste the following Lua code to try it out."),f=u(`<div class="custom-container warning"><p class="custom-container-title">WARNING</p><p>Note that if you are using this in a normal Java application, you should take note of when you close the Lua state. You don&#39;t want to have the Lua state closed before the window is fully shutdown, which will definitely lead to problems.</p></div><div class="language-lua ext-lua line-numbers-mode"><pre class="language-lua"><code>frame <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Frame&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Lua Java Console&quot;</span><span class="token punctuation">)</span>
console <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.TextArea&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
buttonsPanel <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Panel&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
executeButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Execute&quot;</span><span class="token punctuation">)</span>
clearButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Clear&quot;</span><span class="token punctuation">)</span>
exitButton <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.Button&quot;</span><span class="token punctuation">)</span><span class="token punctuation">(</span><span class="token string">&quot;Exit&quot;</span><span class="token punctuation">)</span>

frame<span class="token punctuation">:</span><span class="token function">setSize</span><span class="token punctuation">(</span><span class="token number">600</span><span class="token punctuation">,</span> <span class="token number">300</span><span class="token punctuation">)</span>

buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>executeButton<span class="token punctuation">)</span>
buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>clearButton<span class="token punctuation">)</span>
buttonsPanel<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>exitButton<span class="token punctuation">)</span>

BorderLayout <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">import</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.BorderLayout&quot;</span><span class="token punctuation">)</span>

frame<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>BorderLayout<span class="token punctuation">.</span>NORTH<span class="token punctuation">,</span> console<span class="token punctuation">)</span>
frame<span class="token punctuation">:</span><span class="token function">add</span><span class="token punctuation">(</span>BorderLayout<span class="token punctuation">.</span>SOUTH<span class="token punctuation">,</span> buttonsPanel<span class="token punctuation">)</span>
frame<span class="token punctuation">:</span><span class="token function">pack</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
frame<span class="token punctuation">:</span><span class="token function">show</span><span class="token punctuation">(</span><span class="token punctuation">)</span>

<span class="token comment">--</span>
<span class="token comment">-- Listeners</span>
<span class="token comment">--</span>

executeCallback <span class="token operator">=</span> <span class="token punctuation">{</span>
  actionPerformed <span class="token operator">=</span> <span class="token keyword">function</span><span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span>
    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;execute&quot;</span><span class="token punctuation">)</span>
    <span class="token function">pcall</span><span class="token punctuation">(</span><span class="token function">loadstring</span><span class="token punctuation">(</span>console<span class="token punctuation">:</span><span class="token function">getText</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">)</span>
  <span class="token keyword">end</span>
<span class="token punctuation">}</span>

jproxy <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">proxy</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.event.ActionListener&quot;</span><span class="token punctuation">,</span> executeCallback<span class="token punctuation">)</span>

executeButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span>jproxy<span class="token punctuation">)</span>

clearCallback <span class="token operator">=</span> <span class="token punctuation">{</span>
  actionPerformed <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span>
    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;clear&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    console<span class="token punctuation">:</span><span class="token function">setText</span><span class="token punctuation">(</span><span class="token string">&quot;&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
  <span class="token keyword">end</span>
<span class="token punctuation">}</span>

jproxy <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">proxy</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.event.ActionListener&quot;</span><span class="token punctuation">,</span> clearCallback<span class="token punctuation">)</span>
clearButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span>jproxy<span class="token punctuation">)</span>

exitCallback <span class="token operator">=</span> <span class="token punctuation">{</span>
  actionPerformed <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span>
    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;exit&quot;</span><span class="token punctuation">)</span>
    frame<span class="token punctuation">:</span><span class="token function">setVisible</span><span class="token punctuation">(</span><span class="token keyword">false</span><span class="token punctuation">)</span>
    frame<span class="token punctuation">:</span><span class="token function">dispose</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
  <span class="token keyword">end</span>
<span class="token punctuation">}</span>


jproxyb <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">proxy</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.event.ActionListener&quot;</span><span class="token punctuation">,</span> exitCallback<span class="token punctuation">)</span>

exitButton<span class="token punctuation">:</span><span class="token function">addActionListener</span><span class="token punctuation">(</span>jproxyb<span class="token punctuation">)</span>

closeCallback <span class="token operator">=</span> <span class="token punctuation">{</span>
  windowClosing <span class="token operator">=</span> <span class="token keyword">function</span> <span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span>
    <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;close&quot;</span><span class="token punctuation">)</span>
    frame<span class="token punctuation">:</span><span class="token function">setVisible</span><span class="token punctuation">(</span><span class="token keyword">false</span><span class="token punctuation">)</span>
    frame<span class="token punctuation">:</span><span class="token function">dispose</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
  <span class="token keyword">end</span>
<span class="token punctuation">}</span>

<span class="token keyword">function</span> closeCallback<span class="token punctuation">.</span><span class="token function">windowActivated</span><span class="token punctuation">(</span>this<span class="token punctuation">,</span> ev<span class="token punctuation">)</span>
  <span class="token function">print</span><span class="token punctuation">(</span><span class="token string">&quot;act&quot;</span><span class="token punctuation">)</span>
<span class="token keyword">end</span>

jproxy <span class="token operator">=</span> java<span class="token punctuation">.</span><span class="token function">proxy</span><span class="token punctuation">(</span><span class="token string">&quot;java.awt.event.WindowListener&quot;</span><span class="token punctuation">,</span> closeCallback<span class="token punctuation">)</span>
frame<span class="token punctuation">:</span><span class="token function">addWindowListener</span><span class="token punctuation">(</span>jproxy<span class="token punctuation">)</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,2);function w(q,x){const a=p("RouterLink");return o(),e("div",null,[k,n("div",r,[d,n("p",null,[v,c(a,{to:"/console.html"},{default:i(()=>[m]),_:1}),b])]),f])}var y=t(l,[["render",w],["__file","awt.html.vue"]]);export{y as default};
