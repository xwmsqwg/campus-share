<script setup>
import NoteCard from './NoteCard.vue'
defineProps({ notes: { type: Array, default: () => [] } })
</script>
<template><div class="note-grid"><NoteCard v-for="note in notes" :key="note.id" :note="note" /></div></template>
