import { defineUserConfig, defaultTheme } from 'vuepress'

export default defineUserConfig({
  lang: 'en-US',
  title: 'LuaJava',
  description: 'Lua for Java',
  base: '/luajava/',
  head: [['link', { rel: 'icon', href: '/favicon.png' }]],
  theme: defaultTheme({
    logo: '/lua-java.svg',
    repo: 'https://github.com/gudzpoz/luajava',
    docsBranch: 'main',
    docsDir: 'docs',
    lastUpdated: true,
    contributors: false,
    editLink: true,
    editLinkPattern: ':repo/blob/:branch/:path',
    navbar: [
      { text: 'Home', link: '/', },
      { text: 'Javadoc', link: '/javadoc/', },
      { text: 'Lua', link: 'https://www.lua.org', },
      { text: 'LuaJIT', link: 'https://luajit.org/luajit.html' },
    ],
    sidebarDepth: 3,
    sidebar: [
      {
        text: 'Introduction',
        children: [
          '/index.md',
          '/getting-started.md',
          '/console.md',
        ]
      },
      {
        text: 'Lua API',
        children: [
          '/api.md',
          '/conversions.md',
          '/threadsafety.md',
          '/troubleshooting.md',
        ]
      },
      {
        text: 'Examples',
        children: [
          '/examples/hello-world.md',
          '/examples/awt.md',
        ]
      },
    ],
  }),
  markdown: {
    code: {
      lineNumbers: 10,
    },
  },
  extendsMarkdown (md) {
    md.use(require('markdown-it-footnote'))
  },
})
