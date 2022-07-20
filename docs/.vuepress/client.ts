import { defineClientConfig } from '@vuepress/client'

export default defineClientConfig({
  enhance({ router }) {
    router.addRoute({
      path: '/javadoc/:path(.*)',
      redirect: (route) => {
        const url = `${location.protocol}//${location.host}/luajava/javadoc/${route.params.path}${route.hash}`;
        window.open(url, '_blank');
        return router.currentRoute.value;
      },
    })
  },
})
