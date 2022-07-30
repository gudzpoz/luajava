import{_ as u,r as i,o as d,c as p,a as e,b as a,w as s,e as n,d as r}from"./app.2fe4c2aa.js";const v={},m=e("h1",{id:"proxy-caveats",tabindex:"-1"},[e("a",{class:"header-anchor",href:"#proxy-caveats","aria-hidden":"true"},"#"),n(" Proxy Caveats")],-1),h=n("Both "),k=n("the Java API"),b=n(" and "),g=n("the Lua API"),f=n(" provide a way to create Java proxies that delegate calls to a underlying Lua table."),_=e("div",{class:"language-java ext-java"},[e("pre",{class:"language-java"},[e("code",null,[e("span",{class:"token class-name"},"Lua"),n(),e("span",{class:"token class-name"},"L"),n(),e("span",{class:"token operator"},"="),n(),e("span",{class:"token keyword"},"new"),n(),e("span",{class:"token class-name"},"Lua54"),e("span",{class:"token punctuation"},"("),e("span",{class:"token punctuation"},")"),e("span",{class:"token punctuation"},";"),n(`
`),e("span",{class:"token class-name"},"L"),e("span",{class:"token punctuation"},"."),e("span",{class:"token function"},"run"),e("span",{class:"token punctuation"},"("),e("span",{class:"token string"},`"return { run = function() print('Hello') end }"`),e("span",{class:"token punctuation"},")"),e("span",{class:"token punctuation"},";"),n(`
`),e("span",{class:"token class-name"},"Runnable"),n(" r "),e("span",{class:"token operator"},"="),n(),e("span",{class:"token punctuation"},"("),e("span",{class:"token class-name"},"Runnable"),e("span",{class:"token punctuation"},")"),n(),e("span",{class:"token class-name"},"L"),e("span",{class:"token punctuation"},"."),e("span",{class:"token function"},"createProxy"),e("span",{class:"token punctuation"},"("),e("span",{class:"token keyword"},"new"),n(),e("span",{class:"token class-name"},"Class"),e("span",{class:"token punctuation"},"["),e("span",{class:"token punctuation"},"]"),n(),e("span",{class:"token punctuation"},"{"),e("span",{class:"token class-name"},"Runnable"),e("span",{class:"token punctuation"},"."),e("span",{class:"token keyword"},"class"),e("span",{class:"token punctuation"},"}"),e("span",{class:"token punctuation"},","),n(),e("span",{class:"token class-name"},[n("Lua"),e("span",{class:"token punctuation"},"."),n("Conversion")]),e("span",{class:"token punctuation"},"."),n("SEMI"),e("span",{class:"token punctuation"},")"),e("span",{class:"token punctuation"},";"),n(`
`)])])],-1),y=e("div",{class:"language-lua ext-lua"},[e("pre",{class:"language-lua"},[e("code",null,[n("r "),e("span",{class:"token operator"},"="),n(" java"),e("span",{class:"token punctuation"},"."),e("span",{class:"token function"},"proxy"),e("span",{class:"token punctuation"},"("),e("span",{class:"token string"},"'java.lang.Runnable'"),e("span",{class:"token punctuation"},","),n(),e("span",{class:"token punctuation"},"{"),n(`
  run `),e("span",{class:"token operator"},"="),n(),e("span",{class:"token keyword"},"function"),e("span",{class:"token punctuation"},"("),e("span",{class:"token punctuation"},")"),n(`
    `),e("span",{class:"token function"},"print"),e("span",{class:"token punctuation"},"("),e("span",{class:"token string"},"'Hello'"),e("span",{class:"token punctuation"},")"),n(`
  `),e("span",{class:"token keyword"},"end"),n(`
`),e("span",{class:"token punctuation"},"}"),e("span",{class:"token punctuation"},")"),n(`
`)])])],-1),j=e("p",null,"However, there are a few thing that you might want to take note of.",-1),x={class:"custom-container tip"},w=e("p",{class:"custom-container-title"},"TL;DR",-1),I=e("li",null,[n("Use "),e("code",null,"public"),n(" interfaces.")],-1),L=n("Add "),A={href:"https://search.maven.org/artifact/org.ow2.asm/asm",target:"_blank",rel:"noopener noreferrer"},P=n("ASM"),N=n(" to your classpath / runtime dependencies to rid "),C=e("code",null,"illegal reflective access",-1),M=n(" warnings."),R=r('<h2 id="access-levels" tabindex="-1"><a class="header-anchor" href="#access-levels" aria-hidden="true">#</a> Access Levels</h2><p>All interfaces implemented should be visible to all classes (i.e., <code>public</code>), although things might work for package-private interfaces depending on JVM security settings.</p><h2 id="default-methods" tabindex="-1"><a class="header-anchor" href="#default-methods" aria-hidden="true">#</a> Default Methods</h2><h3 id="illegal-reflective-access" tabindex="-1"><a class="header-anchor" href="#illegal-reflective-access" aria-hidden="true">#</a> Illegal reflective access</h3><p>Java 8 brings default methods in interfaces. However, the official reflection API is not adjusted accordingly, making it a real pain to reflectively call the default methods from a proxy.</p>',5),S={class:"custom-container tip"},J=e("p",{class:"custom-container-title"},"TIP",-1),W=n("If you are interested, there is an article on this: "),G={href:"https://blog.jooq.org/correct-reflective-access-to-interface-default-methods-in-java-8-9-10/",target:"_blank",rel:"noopener noreferrer"},D=n("Correct Reflective Access to Interface Default Methods in Java 8, 9, 10"),T=n(". And you might want to check out the workarounds used by Spring: "),q={href:"https://github.com/spring-projects/spring-data-commons/blob/6a23723f07669e5d4031b3378b3af40e0d15eb82/src/main/java/org/springframework/data/projection/DefaultMethodInvokingMethodInterceptor.java",target:"_blank",rel:"noopener noreferrer"},E=n("DefaultMethodInvokingMethodInterceptor.java"),O=n("."),U=r(`<p>We use <code>unreflectSpecial</code> to find the default methods, which is only allowed if the caller is a subclass / subinterface. Since a proxy itself is not considered as a subclass of the implemented interfaces, you will very likely be seeing the following reflection warnings.</p><div class="language-text ext-text"><pre class="language-text"><code>WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access using Lookup on party.iroiro.luajava.LuaProxy (file:...) to interface ...
WARNING: Please consider reporting this to the maintainers of party.iroiro.luajava.LuaProxy
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
</code></pre></div><h3 id="workaround" tabindex="-1"><a class="header-anchor" href="#workaround" aria-hidden="true">#</a> Workaround</h3><p>A way to workaround this is to introduce some intermediate interfaces:</p><div class="language-text ext-text line-numbers-mode"><pre class="language-text"><code>Original hierarchy:
  java.lang.Object
  |\\
  | \\- party.iroiro.luajava.LuaProxy -&gt; has no access
   \\
    \\- the implemented interface1
    \\- the implemented interface2

New hierarchy:
  java.lang.Object
  |\\
  | \\- party.iroiro.luajava.LuaProxy
   \\
    \\- interface1
    |   \\- the actually implemented interface1 bridge -&gt; has access
    |
    \\- interface2
        \\- the actually implemented interface2 bridge -&gt; has access
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>We programmatically generate the intermediate interfaces, injecting some static methods to look up the interfaces and let the final proxy object implement the intermediate interfaces instead. By doing so, we finally obtain &quot;legal reflective access&quot;.</p>`,6),V=n("The above requires the use of "),B={href:"https://asm.ow2.io",target:"_blank",rel:"noopener noreferrer"},H=n("the ASM library"),$=n(" to dynamically create interfaces, which is too heavyweight for this library. You will need to enable it by manually adding the ASM dependency."),Y=r(`<ul><li>To introduce the ASM workaround: <ol><li>Add ASM to your runtime dependencies:<div class="language-groovy ext-groovy"><pre class="language-groovy"><code>runtimeOnly <span class="token string">&#39;org.ow2.asm:asm:9.3&#39;</span>
</code></pre></div></li><li>Ensure that the <code>luajava_lookup</code> system property is either not set or set to <code>asm</code>.</li></ol></li><li>To use the default approach, either: <ul><li>Remove ASM from the classpath / dependencies.</li><li>Or set the <code>luajava_lookup</code> system property to some other value:<div class="language-java ext-java"><pre class="language-java"><code><span class="token class-name">System</span><span class="token punctuation">.</span><span class="token function">setProperty</span><span class="token punctuation">(</span><span class="token string">&quot;luajava_lookup&quot;</span><span class="token punctuation">,</span> <span class="token string">&quot;no&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre></div></li></ul></li></ul>`,1),z={class:"custom-container tip"},F=e("p",{class:"custom-container-title"},"Try things out",-1),K=n("The "),Q=n("interactive console"),X=n(" bundles the ASM library with it. You don't need to enable the ASM workaround:"),Z=e("div",{class:"language-lua ext-lua line-numbers-mode"},[e("pre",{class:"language-lua"},[e("code",null,[n("iterImpl "),e("span",{class:"token operator"},"="),n(),e("span",{class:"token punctuation"},"{"),n(`
  next `),e("span",{class:"token operator"},"="),n(),e("span",{class:"token keyword"},"function"),e("span",{class:"token punctuation"},"("),e("span",{class:"token punctuation"},")"),n(`
    i `),e("span",{class:"token operator"},"="),n(" i "),e("span",{class:"token operator"},"-"),n(),e("span",{class:"token number"},"1"),n(`
    `),e("span",{class:"token keyword"},"return"),n(` i
  `),e("span",{class:"token keyword"},"end"),e("span",{class:"token punctuation"},","),n(`
  hasNext `),e("span",{class:"token operator"},"="),n(),e("span",{class:"token keyword"},"function"),e("span",{class:"token punctuation"},"("),e("span",{class:"token punctuation"},")"),n(`
    `),e("span",{class:"token keyword"},"return"),n(" i "),e("span",{class:"token operator"},">"),n(),e("span",{class:"token number"},"0"),n(`
  `),e("span",{class:"token keyword"},"end"),n(`
`),e("span",{class:"token punctuation"},"}"),n(`

iter `),e("span",{class:"token operator"},"="),n(" java"),e("span",{class:"token punctuation"},"."),e("span",{class:"token function"},"proxy"),e("span",{class:"token punctuation"},"("),e("span",{class:"token string"},"'java.util.Iterator'"),e("span",{class:"token punctuation"},","),n(" iterImpl"),e("span",{class:"token punctuation"},")"),n(`

`),e("span",{class:"token comment"},"-- The default `remove` throws an UnsupportedOperationException"),n(`
iter`),e("span",{class:"token punctuation"},":"),e("span",{class:"token function"},"remove"),e("span",{class:"token punctuation"},"("),e("span",{class:"token punctuation"},")"),n(`
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"})])],-1),ee=e("div",{class:"language-shell-session ext-shell-session line-numbers-mode"},[e("pre",{class:"language-shell-session"},[e("code",null,[e("span",{class:"token command"},[e("span",{class:"token shell-symbol important"},"$"),n(),e("span",{class:"token bash language-bash"},[n("java --illegal-access"),e("span",{class:"token operator"},"="),n("debug -jar example-all.jar")])]),n(`
`),e("span",{class:"token output"},`Lua Version: 5.1
Running Lua 5.1
>>> iterImpl = {
  >   next = function()
  >     i = i - 1
  >     return i
  >   end,
  >   hasNext = function()
  >     return i > 0
  >   end
  > }
  > 
  > iter = java.proxy('java.util.Iterator', iterImpl)
>>> iter:remove()
java.lang.UnsupportedOperationException: remove
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"})])],-1),ne=e("div",{class:"language-shell-session ext-shell-session line-numbers-mode"},[e("pre",{class:"language-shell-session"},[e("code",null,[e("span",{class:"token command"},[e("span",{class:"token shell-symbol important"},"$"),n(),e("span",{class:"token bash language-bash"},[n("java -Dluajava_lookup"),e("span",{class:"token operator"},"="),n("no --illegal-access"),e("span",{class:"token operator"},"="),n("debug -jar example/build/libs/example-all.jar   ")])]),n(`
`),e("span",{class:"token output"},`Lua Version: 5.1
Running Lua 5.1
>>> iterImpl = {
  >   next = function()
  >     i = i - 1
  >     return i
  >   end,
  >   hasNext = function()
  >     return i > 0
  >   end
  > }
  > 
  > iter = java.proxy('java.util.Iterator', iterImpl)
>>> iter:remove()
WARNING: Illegal reflective access using Lookup on party.iroiro.luajava.util.NastyLookupProvider (file:/tmp/example-all.jar) to interface java.util.Iterator
	at party.iroiro.luajava.util.NastyLookupProvider.lookup(NastyLookupProvider.java:72)
	at party.iroiro.luajava.util.ClassUtils.invokeDefault(ClassUtils.java:314)
	at party.iroiro.luajava.LuaProxy.callDefaultMethod(LuaProxy.java:77)
	at party.iroiro.luajava.LuaProxy.invoke(LuaProxy.java:39)
	at com.sun.proxy.$Proxy1.remove(Unknown Source)
	at party.iroiro.luajava.JuaAPI.methodInvoke(JuaAPI.java:546)
	at party.iroiro.luajava.JuaAPI.methodInvoke(JuaAPI.java:490)
	at party.iroiro.luajava.JuaAPI.objectInvoke(JuaAPI.java:203)
	at party.iroiro.luajava.Lua51Natives.luaL_dostring(Native Method)
	at party.iroiro.luajava.AbstractLua.run(AbstractLua.java:490)
	at party.iroiro.luajava.Console.startInteractive(Console.java:64)
	at party.iroiro.luajava.Console.main(Console.java:31)
java.lang.UnsupportedOperationException: remove
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"})])],-1);function ae(se,te){const c=i("RouterLink"),t=i("CodeGroupItem"),o=i("CodeGroup"),l=i("ExternalLinkIcon");return d(),p("div",null,[m,e("p",null,[h,a(c,{to:"/javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class%5B%5D,party.iroiro.luajava.Lua.Conversion)"},{default:s(()=>[k]),_:1}),b,a(c,{to:"/api.html#proxy-jclass-table-function"},{default:s(()=>[g]),_:1}),f]),a(o,null,{default:s(()=>[a(t,{title:"Java API"},{default:s(()=>[_]),_:1})]),_:1}),a(o,null,{default:s(()=>[a(t,{title:"Lua API"},{default:s(()=>[y]),_:1})]),_:1}),j,e("div",x,[w,e("ol",null,[I,e("li",null,[L,e("a",A,[P,a(l)]),N,C,M])])]),R,e("div",S,[J,e("p",null,[W,e("a",G,[D,a(l)]),T,e("a",q,[E,a(l)]),O])]),U,e("p",null,[V,e("a",B,[H,a(l)]),$]),Y,e("div",z,[F,e("p",null,[K,a(c,{to:"/console.html"},{default:s(()=>[Q]),_:1}),X]),a(o,null,{default:s(()=>[a(t,{title:"Lua Snippet"},{default:s(()=>[Z]),_:1})]),_:1}),a(o,null,{default:s(()=>[a(t,{title:"With the workaround"},{default:s(()=>[ee]),_:1}),a(t,{title:"Without"},{default:s(()=>[ne]),_:1})]),_:1})])])}var le=u(v,[["render",ae],["__file","proxy.html.vue"]]);export{le as default};
