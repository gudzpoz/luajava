# Interactive Console

We build a tiny console application with [JLine3](https://github.com/jline/jline3) every release. You may download the `example-all.jar` from our [Release page](https://github.com/gudzpoz/luajava/releases). The jar bundles desktop natives with it, so you should be about to play around with it on your own computer.

<script setup>
import 'asciinema-player/dist/bundle/asciinema-player.css'
import * as AsciinemaPlayer from 'asciinema-player'
import { ref, onMounted } from 'vue'

const cinema = ref(null)

onMounted(() => {
  AsciinemaPlayer.create('/example.cast', cinema.value, {
    rows: 12,
  })
})
</script>

<div ref="cinema"></div>

`Lua Version` can be any of `5.1`, `5.2`, `5.3`, `5.4` or `jit`.
