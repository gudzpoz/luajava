import { defineClientConfig } from '@vuepress/client'

export default defineClientConfig({
  enhance({ router }) {
    router.addRoute({
      path: '/javadoc/:catchAll(.*)',
      redirect: () => {
        const url = `${location.protocol}//${location.host}/luajava/javadoc/index.html`
        window.open(url, '_blank')
        return router.currentRoute.value
      },
    })
  },
})
