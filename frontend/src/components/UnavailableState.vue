<script setup>
defineProps({
  title: { type: String, default: '当前功能暂未开发' },
  message: { type: String, default: '后端接口接入后，这里会开放完整功能。' }
})
</script>

<template>
  <section class="unavailable-state" role="status">
    <p class="eyebrow">COMING SOON</p>
    <h1>{{ title }}</h1>
    <p>{{ message }}</p>
</section>
</template>
