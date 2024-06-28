import{_ as s,o as n,c as e,e as a}from"./app-BfXweHhW.js";const i={},l=a(`<h1 id="gnu-c-library-glibc-compatibility" tabindex="-1"><a class="header-anchor" href="#gnu-c-library-glibc-compatibility"><span>GNU C Library (glibc) Compatibility</span></a></h1><h2 id="glibc" tabindex="-1"><a class="header-anchor" href="#glibc"><span>glibc</span></a></h2><p>The GNU C Library (glibc) is using <a href="https://refspecs.linuxfoundation.org/LSB_3.0.0/LSB-PDA/LSB-PDA.junk/symversion.html" target="_blank" rel="noopener noreferrer">symbol versioning</a> to provide backward compatibility for binaries compiled with older glibc. We use <code>.symver</code> and <a href="https://github.com/NixOS/patchelf" target="_blank" rel="noopener noreferrer">patchelf</a> to compile our binaries with newer glibc while keeping compatibility with older ones.</p><p>The required glibc versions are listed below:</p><ul><li><code>linux64</code>:<div class="language-text line-numbers-mode" data-ext="text" data-title="text"><pre class="language-text"><code><span class="line">GLIBC_2.11</span>
<span class="line">GLIBC_2.2.5</span>
<span class="line">GLIBC_2.3</span>
<span class="line">GLIBC_2.3.4</span>
<span class="line">GLIBC_2.4</span>
<span class="line">GLIBC_2.7</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div></li><li><code>linux32</code>:<div class="language-text line-numbers-mode" data-ext="text" data-title="text"><pre class="language-text"><code><span class="line">GLIBC_2.0</span>
<span class="line">GLIBC_2.1</span>
<span class="line">GLIBC_2.11</span>
<span class="line">GLIBC_2.1.3</span>
<span class="line">GLIBC_2.2</span>
<span class="line">GLIBC_2.3</span>
<span class="line">GLIBC_2.3.4</span>
<span class="line">GLIBC_2.4</span>
<span class="line">GLIBC_2.7</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div></li><li><code>linuxarm64</code>:<div class="language-text line-numbers-mode" data-ext="text" data-title="text"><pre class="language-text"><code><span class="line">GLIBC_2.0</span>
<span class="line">GLIBC_2.17</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div></div></div></li><li><code>linuxarm32</code>:<div class="language-text line-numbers-mode" data-ext="text" data-title="text"><pre class="language-text"><code><span class="line">GLIBC_2.11</span>
<span class="line">GLIBC_2.4</span>
<span class="line">GLIBC_2.7</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div></li></ul><h2 id="musl" tabindex="-1"><a class="header-anchor" href="#musl"><span>musl</span></a></h2><p>For example, on <a href="https://alpinelinux.org/" target="_blank" rel="noopener noreferrer">Alpine Linux</a>, you will very likely need <a href="https://git.adelielinux.org/adelie/gcompat" target="_blank" rel="noopener noreferrer">gcompat</a> (as well as <code>libstdc++</code>) to use the library.</p><p>Here is a <code>Dockerfile</code> snippet that is used to test this library on Alpine.</p><div class="language-docker line-numbers-mode" data-ext="docker" data-title="docker"><pre class="language-docker"><code><span class="line"><span class="token instruction"><span class="token keyword">FROM</span> eclipse-temurin:11-alpine</span></span>
<span class="line"></span>
<span class="line"><span class="token instruction"><span class="token keyword">RUN</span> mkdir /opt/app</span></span>
<span class="line"><span class="token instruction"><span class="token keyword">COPY</span> example-all.jar /opt/app</span></span>
<span class="line"><span class="token instruction"><span class="token keyword">RUN</span> apk add gcompat</span></span>
<span class="line"><span class="token instruction"><span class="token keyword">RUN</span> apk add libstdc++</span></span>
<span class="line"><span class="token instruction"><span class="token keyword">RUN</span> ldd --version || true</span></span>
<span class="line"><span class="token instruction"><span class="token keyword">CMD</span> [<span class="token string">&quot;java&quot;</span>, <span class="token string">&quot;-jar&quot;</span>, <span class="token string">&quot;/opt/app/example-all.jar&quot;</span>, <span class="token string">&quot;--test&quot;</span>]</span></span>
<span class="line"></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,9),t=[l];function c(r,d){return n(),e("div",null,t)}const o=s(i,[["render",c],["__file","glibc.html.vue"]]),u=JSON.parse('{"path":"/glibc.html","title":"GNU C Library (glibc) Compatibility","lang":"en-US","frontmatter":{},"headers":[{"level":2,"title":"glibc","slug":"glibc","link":"#glibc","children":[]},{"level":2,"title":"musl","slug":"musl","link":"#musl","children":[]}],"git":{"updatedTime":1719561160000},"filePathRelative":"glibc.md"}');export{o as comp,u as data};
