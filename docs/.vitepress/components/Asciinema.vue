<script setup>
import 'asciinema-player/dist/bundle/asciinema-player.css';
import { ref, onMounted } from 'vue';

const cinema = ref(null);
const props = defineProps(['file']);

onMounted(() => {
  if (!import.meta.env.SSR) {
    import('asciinema-player').then(AsciinemaPlayer =>
      AsciinemaPlayer.create(props.file, cinema.value, {
        rows: 12,
      })
    );
  }
});
</script>

<template>
<div ref="cinema"></div>
</template>
