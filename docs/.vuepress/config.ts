import { defineUserConfig } from 'vuepress'
import { viteBundler } from '@vuepress/bundler-vite'
import { defaultTheme } from '@vuepress/theme-default'
import { registerComponentsPlugin } from '@vuepress/plugin-register-components'
import { path } from '@vuepress/utils'

import markdownItFootnote from 'markdown-it-footnote'

export default defineUserConfig({
  lang: 'en-US',
  title: 'LuaJava',
  description: 'Lua for Java',
  base: '/luajava/',
  head: [['link', { rel: 'icon', href: '/luajava/favicon.png' }]],
  bundler: viteBundler(),
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
      { text: 'Javadoc', link: '/javadoc/index.html', },
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
          '/jsr223.md',
          '/jpms.md',
        ]
      },
      {
        text: 'Lua API',
        children: [
          '/api.md',
          '/conversions.md',
          '/proxy.md',
          '/threadsafety.md',
        ]
      },
      {
        text: 'Java API',
        children: [
          '/java.md',
          '/c-api.md',
        ],
      },
      {
        text: 'Examples',
        children: [
          '/examples/compat.md',
          '/examples/hello-world.md',
          '/examples/hello-world-mod.md',
          '/examples/awt.md',
          '/examples/modules.md',
        ]
      },
      {
        text: 'Troubleshooting',
        children: [
          '/troubleshooting.md',
          '/rtld.md',
          '/glibc.md',
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
    md.use(markdownItFootnote)
  },
  plugins: [
    registerComponentsPlugin({
      components: {
        Asciinema: path.resolve(__dirname, './components/Asciinema.vue'),
        Matrix: path.resolve(__dirname, './components/Matrix.vue'),
      },
    })
  ],
})
