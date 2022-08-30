import{_ as o,r as i,o as l,c as r,a as n,b as t,e,d as a}from"./app.d1231d37.js";const c={},d=n("h1",{id:"gnu-c-library-glibc-compatibility",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#gnu-c-library-glibc-compatibility","aria-hidden":"true"},"#"),e(" GNU C Library (glibc) Compatibility")],-1),p=n("h2",{id:"glibc",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#glibc","aria-hidden":"true"},"#"),e(" glibc")],-1),_=e("The GNU C Library (glibc) is using "),u={href:"https://refspecs.linuxfoundation.org/LSB_3.0.0/LSB-PDA/LSB-PDA.junk/symversion.html",target:"_blank",rel:"noopener noreferrer"},h=e("symbol versioning"),g=e(" to provide backward compatibility for binaries compiled with older glibc. We use "),k=n("code",null,".symver",-1),b=e(" and "),x={href:"https://github.com/NixOS/patchelf",target:"_blank",rel:"noopener noreferrer"},m=e("patchelf"),L=e(" to compile our binaries with newer glibc while keeping compatibility with older ones."),B=a(`<p>The required glibc versions are listed below:</p><ul><li><code>linux64</code>:<div class="language-text ext-text"><pre class="language-text"><code>GLIBC_2.11
GLIBC_2.2.5
GLIBC_2.3
GLIBC_2.3.4
GLIBC_2.4
GLIBC_2.7
</code></pre></div></li><li><code>linux32</code>:<div class="language-text ext-text"><pre class="language-text"><code>GLIBC_2.0
GLIBC_2.1
GLIBC_2.11
GLIBC_2.1.3
GLIBC_2.2
GLIBC_2.3
GLIBC_2.3.4
GLIBC_2.4
GLIBC_2.7
</code></pre></div></li><li><code>linuxarm64</code>:<div class="language-text ext-text"><pre class="language-text"><code>GLIBC_2.0
GLIBC_2.17
</code></pre></div></li><li><code>linuxarm32</code>:<div class="language-text ext-text"><pre class="language-text"><code>GLIBC_2.11
GLIBC_2.4
GLIBC_2.7
</code></pre></div></li></ul><h2 id="musl" tabindex="-1"><a class="header-anchor" href="#musl" aria-hidden="true">#</a> musl</h2>`,3),C=e("For example, on "),f={href:"https://alpinelinux.org/",target:"_blank",rel:"noopener noreferrer"},y=e("Alpine Linux"),v=e(", you will very likely need "),G={href:"https://git.adelielinux.org/adelie/gcompat",target:"_blank",rel:"noopener noreferrer"},I=e("gcompat"),w=e(" (as well as "),N=n("code",null,"libstdc++",-1),q=e(") to use the library."),U=a(`<p>Here is a <code>Dockerfile</code> snippet that is used to test this library on Alpine.</p><div class="language-docker ext-docker"><pre class="language-docker"><code><span class="token instruction"><span class="token keyword">FROM</span> eclipse-temurin:11-alpine</span>

<span class="token instruction"><span class="token keyword">RUN</span> mkdir /opt/app</span>
<span class="token instruction"><span class="token keyword">COPY</span> example-all.jar /opt/app</span>
<span class="token instruction"><span class="token keyword">RUN</span> apk add gcompat</span>
<span class="token instruction"><span class="token keyword">RUN</span> apk add libstdc++</span>
<span class="token instruction"><span class="token keyword">RUN</span> ldd --version || true</span>
<span class="token instruction"><span class="token keyword">CMD</span> [<span class="token string">&quot;java&quot;</span>, <span class="token string">&quot;-jar&quot;</span>, <span class="token string">&quot;/opt/app/example-all.jar&quot;</span>, <span class="token string">&quot;--test&quot;</span>]</span>
</code></pre></div>`,2);function j(R,S){const s=i("ExternalLinkIcon");return l(),r("div",null,[d,p,n("p",null,[_,n("a",u,[h,t(s)]),g,k,b,n("a",x,[m,t(s)]),L]),B,n("p",null,[C,n("a",f,[y,t(s)]),v,n("a",G,[I,t(s)]),w,N,q]),U])}var D=o(c,[["render",j],["__file","glibc.html.vue"]]);export{D as default};
