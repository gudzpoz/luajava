import { defineConfig } from 'vitepress';

import markdownItFootnote from 'markdown-it-footnote';
import { pagefindPlugin } from 'vitepress-plugin-pagefind';

export default defineConfig({
  lang: 'en-US',
  title: 'LuaJava',
  description: 'Lua for Java',
  base: '/luajava/',
  head: [['link', { rel: 'icon', href: '/luajava/favicon.png' }]],
  themeConfig: {
    logo: '/lua-java.svg',
    lastUpdated: {},
    editLink: {
      pattern: 'https://github.com/gudzpoz/luajava/blob/main/docs/:path',
    },
    socialLinks: [
      { icon: 'github', link: 'https://github.com/gudzpoz/luajava' },
    ],
    nav: [
      { text: 'Home', link: '/', },
      { text: 'Javadoc', link: '/javadoc/index.html', target: '_self' },
      { text: 'Lua', link: 'https://www.lua.org', },
      { text: 'LuaJIT', link: 'https://luajit.org/luajit.html' },
    ],

    outline: 3,
    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'What is LuaJava?', link: '/index.md' },
          { text: 'Getting Started', link: '/getting-started.md' },
          { text: 'Interactive Console', link: '/console.md' },
          { text: 'JSR 223', link: '/jsr223.md' },
          { text: 'Java 9 Platform Module System', link: '/jpms.md' },
        ]
      },
      {
        text: 'Java API',
        items: [
          { text: 'Java API', link: '/java.md' },
          { text: 'Lua Stack-based C API', link: '/c-api.md' },
        ],
      },
      {
        text: 'Lua API',
        items: [
          { text: 'Lua API', link: '/api.md' },
          { text: 'Type Conversions', link: '/conversions.md' },
          { text: 'Proxy Caveats', link: '/proxy.md' },
          { text: 'Thread Safety', link: '/threadsafety.md' },
        ]
      },
      {
        text: 'Examples',
        items: [
          { text: 'LuaJava API Compatibility', link: '/examples/compat.md' },
          { text: 'Hello World', link: '/examples/hello-world.md' },
          { text: 'Advanced Hello World', link: '/examples/hello-world-mod.md' },
          { text: 'AWT Example', link: '/examples/awt.md' },
          { text: 'Java-Side Modules', link: '/examples/modules.md' },
        ]
      },
      {
        text: 'Troubleshooting',
        items: [
          { text: 'Troubleshooting', link: '/troubleshooting.md' },
          { text: 'Using Binary Lua Libraries', link: '/rtld.md' },
          { text: 'GNU C Library (glibc) Compatibility', link: '/glibc.md' },
        ]
      },
    ],

    externalLinkIcon: true,
  },

  markdown: {
    lineNumbers: true,
    config (md) {
      md.use(markdownItFootnote);
    },
  },

  vite: {
    plugins: [pagefindPlugin()],
  },
});
