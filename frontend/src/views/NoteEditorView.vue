<script setup>
import { onMounted, ref } from 'vue'; import { useRoute, useRouter, RouterLink } from 'vue-router'; import NoteEditor from '../components/NoteEditor.vue'; import UnavailableState from '../components/UnavailableState.vue'; import { createNote, fetchNote, isUnavailableError, updateNote, uploadNoteImage } from '../api'; import { toChineseErrorMessage } from '../api/status'
const route = useRoute(); const router = useRouter(); const note = ref({}); const loading = ref(false); const error = ref(''); const unavailable = ref(false); const editing = Boolean(route.params.id)
onMounted(async () => { if (!editing) return; try { note.value = await fetchNote(route.params.id) } catch (reason) { error.value = toChineseErrorMessage(reason); unavailable.value = isUnavailableError(reason) } })
async function submit(payload) {
  error.value = ''; unavailable.value = false; loading.value = true
  try {
    const saved = editing ? await updateNote(route.params.id, payload) : await createNote(payload)
    if (saved?.id && payload.imageFiles?.length) {
      for (const file of payload.imageFiles) await uploadNoteImage(saved.id, file)
    }
    router.push(`/notes/${saved.id}`)
  } catch (reason) { error.value = toChineseErrorMessage(reason); unavailable.value = isUnavailableError(reason) } finally { loading.value = false }
}
</script>
<template><section class="editor-page"><div class="editor-header"><div><RouterLink to="/" class="back-link">← 返回</RouterLink><p class="eyebrow">{{ editing ? 'EDIT NOTE' : 'CREATE NOTE' }}</p><h1>{{ editing ? '编辑这篇笔记' : '写下今天的校园' }}</h1><p>分享一段经历、一个地点，或只是此刻的心情。</p></div></div><UnavailableState v-if="unavailable" /><div v-else class="editor-shell"><div><p v-if="error" class="form-error editor-error">{{ error }}</p><NoteEditor :initial="note" :submitting="loading" @submit="submit" /></div><aside class="editor-tip"><p class="eyebrow">A LITTLE TIP</p><h3>好的笔记不需要很长。</h3><p>从一个具体的瞬间开始：你看见了什么，听见了什么，或者今天为什么想把它写下来。</p></aside></div></section></template>
