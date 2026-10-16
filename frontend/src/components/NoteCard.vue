<script setup>
import { RouterLink } from 'vue-router'
defineProps({ note: { type: Object, required: true } })
</script>
<template>
  <RouterLink :to="`/notes/${note.id}`" class="note-card">
    <div class="note-image"><img v-if="note.cover" :src="note.cover" :alt="note.title" /><div v-else class="note-image-placeholder" aria-label="暂无图片">暂无图片</div><span class="note-views">◉ {{ note.views }}</span></div>
    <div class="note-card-body"><div class="tag-row"><span v-for="tag in note.tags" :key="tag">{{ tag }}</span></div><h3>{{ note.title }}</h3><p>{{ note.summary }}</p><div class="note-meta"><span><img v-if="note.avatar" :src="note.avatar" alt="" /><span v-else class="avatar-placeholder" aria-hidden="true">{{ (note.author || '匿').slice(0, 1) }}</span>{{ note.author || `用户 ${note.userId || ''}` }}</span><span>♡ {{ note.likes }}</span></div></div>
  </RouterLink>
</template>
